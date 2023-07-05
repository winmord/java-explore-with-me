package ru.practicum.main.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestsMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestInfo;
import ru.practicum.main.request.repository.RequestsRepository;
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
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден."));
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .status(RequestStatus.PENDING)
                .build();

        return RequestsMapper.toParticipationRequestDto(requestsRepository.save(request));
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        usersRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден."));
        Request request = requestsRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Запрос " + requestId + " не найден."));
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
        usersRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден."));
        eventsRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));

        Collection<Request> requests = requestsRepository.findAllByRequesterIdAndEventId(userId, eventId);

        return requests.stream()
                .map(RequestsMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
