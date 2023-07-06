package ru.practicum.main.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.error.EditingErrorException;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.enums.EventUpdateRequestStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestsMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestInfo;
import ru.practicum.main.request.repository.RequestsRepository;
import ru.practicum.main.user.event.EventRequestStatusUpdateRequest;
import ru.practicum.main.user.event.EventRequestStatusUpdateResult;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UsersRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRequestsService {
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;

    public UserRequestsService(RequestsRepository requestsRepository,
                               UsersRepository usersRepository,
                               EventsRepository eventsRepository) {
        this.requestsRepository = requestsRepository;
        this.usersRepository = usersRepository;
        this.eventsRepository = eventsRepository;
    }

    public Collection<ParticipationRequestDto> getRequests(Long userId) {
        Collection<Request> requests = requestsRepository.findAllByRequesterId(userId);
        log.info("Запрошено {} запросов", requests.size());

        return requests.stream()
                .map(RequestsMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = checkUserExists(userId);
        Event event = checkEventExists(eventId);

        if (!requestsRepository.findAllByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new EditingErrorException("Запрос от пользователя " + userId + " на участие в событии " + eventId + " уже существует.");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new EditingErrorException("Инициатор события не может добавлять запрос на участие в нём.");
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new EditingErrorException("Событие " + eventId + " не опубликовано.");
        }

        RequestStatus status = RequestStatus.PENDING;
        if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
            status = RequestStatus.CONFIRMED;
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .status(status)
                .build();

        return RequestsMapper.toParticipationRequestDto(requestsRepository.save(request));
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExists(userId);

        Request request = requestsRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос " + requestId + " не найден."));

        if (RequestStatus.CONFIRMED.equals(request.getStatus())) {
            throw new EditingErrorException("Заявка " + requestId + " уже принята.");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestsMapper.toParticipationRequestDto(requestsRepository.save(request));
    }


    public Map<Long, Integer> getConfirmedRequests(Collection<Event> events) {
        Collection<RequestInfo> requestInfoCollection = requestsRepository.getConfirmedRequests(
                events.stream()
                        .map(Event::getId)
                        .collect(Collectors.toList())
        );

        Map<Long, Integer> confirmedRequests = new HashMap<>();

        for (RequestInfo requestInfo : requestInfoCollection) {
            confirmedRequests.put(requestInfo.getEventId(), requestInfo.getConfirmedRequestsCount().intValue());
        }

        return confirmedRequests;
    }

    public Collection<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        checkUserExists(userId);
        Event event = checkEventExists(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EditingErrorException("Пользователь " + userId + " не является владельцем события " + eventId);
        }

        Collection<Request> requests = requestsRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(RequestsMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateUserEventRequest(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        checkUserExists(userId);
        Event event = checkEventExists(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EditingErrorException("Пользователь " + userId + " не является владельцем события " + eventId);
        }

        Collection<Request> requests = requestsRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        for (Request request : requests) {
            if (!RequestStatus.PENDING.equals(request.getStatus())) {
                throw new EditingErrorException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }

            EventUpdateRequestStatus updateStatus = eventRequestStatusUpdateRequest.getStatus();
            if (EventUpdateRequestStatus.CONFIRMED.equals(updateStatus)) {
                request.setStatus(RequestStatus.CONFIRMED);
                requestsRepository.save(request);
                eventRequestStatusUpdateResult.getConfirmedRequests().add(RequestsMapper.toParticipationRequestDto(request));
            }

            if (EventUpdateRequestStatus.REJECTED.equals(updateStatus)) {
                if (RequestStatus.CONFIRMED.equals(request.getStatus())) {
                    throw new EditingErrorException("Нельзя отменить уже подтверждённый запрос.");
                }
                request.setStatus(RequestStatus.REJECTED);
                requestsRepository.save(request);
                eventRequestStatusUpdateResult.getRejectedRequests().add(RequestsMapper.toParticipationRequestDto(request));
            }
        }

        return eventRequestStatusUpdateResult;
    }

    private User checkUserExists(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден."));
    }

    private Event checkEventExists(Long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));
    }
}
