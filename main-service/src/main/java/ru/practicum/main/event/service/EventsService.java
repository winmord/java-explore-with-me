package ru.practicum.main.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.HitClient;
import ru.practicum.common_dto.ViewStatsDto;
import ru.practicum.main.admin.user.AdminUsersService;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.service.CategoriesService;
import ru.practicum.main.error.EditingErrorException;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.enums.AdminUpdateEventState;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.enums.UserUpdateEventState;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.UserRequestsService;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.event.EventRequestStatusUpdateRequest;
import ru.practicum.main.user.event.EventRequestStatusUpdateResult;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.validation.PagingParametersChecker;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class EventsService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventsRepository eventsRepository;
    private final AdminUsersService adminUsersService;
    private final CategoriesService categoriesService;
    private final LocationsService locationsService;
    private final UserRequestsService userRequestsService;
    private final HitClient hitClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventsService(EventsRepository eventsRepository,
                         AdminUsersService adminUsersService,
                         CategoriesService categoriesService,
                         LocationsService locationsService,
                         UserRequestsService userRequestsService,
                         HitClient hitClient) {
        this.eventsRepository = eventsRepository;
        this.adminUsersService = adminUsersService;
        this.categoriesService = categoriesService;
        this.locationsService = locationsService;
        this.userRequestsService = userRequestsService;
        this.hitClient = hitClient;
    }

    public Collection<EventFullDto> getAdminEvents(Collection<Long> users,
                                                   Collection<EventState> states,
                                                   Collection<Long> categories,
                                                   String rangeStart,
                                                   String rangeEnd,
                                                   Integer from,
                                                   Integer size) throws ValidationException {
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, FORMATTER);
        checkDateValidity(start, end);

        PagingParametersChecker.check(from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        Collection<Event> events = eventsRepository.getEvents(users, states, categories, start, end, pageable).toList();
        Map<Long, Integer> views = getViews(events);
        Map<Long, Integer> confirmedRequests = userRequestsService.getConfirmedRequests(events);

        Collection<EventFullDto> eventFullDtos = new ArrayList<>();

        for (Event event : events) {
            Integer viewsCount = views.get(event.getId());
            Integer confirmedRequestsCount = confirmedRequests.get(event.getId());
            EventFullDto eventFullDto = EventMapper.toEventFullDto(
                    event,
                    viewsCount == null ? 0 : viewsCount,
                    confirmedRequestsCount == null ? 0 : confirmedRequestsCount
            );

            eventFullDtos.add(eventFullDto);
        }

        log.info("Запрошено {} событий", eventFullDtos.size());

        return eventFullDtos;
    }

    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));

        LocalDateTime updatedEventDate = updateEventAdminRequest.getEventDate();
        if (updatedEventDate != null) {
            if (updatedEventDate.isBefore(LocalDateTime.now().plusHours(1)))
                throw new EditingErrorException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");

            event.setEventDate(updatedEventDate);
        }

        AdminUpdateEventState updatedAdminEventState = updateEventAdminRequest.getStateAction();
        if (updatedAdminEventState != null) {
            if (!event.getState().equals(EventState.PENDING) && updatedAdminEventState.equals(AdminUpdateEventState.PUBLISH_EVENT)) {
                throw new EditingErrorException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
            }

            if (event.getState().equals(EventState.PUBLISHED) && updatedAdminEventState.equals(AdminUpdateEventState.REJECT_EVENT)) {
                throw new EditingErrorException("Событие можно отклонить, только если оно еще не опубликовано.");
            }

            if (updatedAdminEventState.equals(AdminUpdateEventState.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }

            if (updatedAdminEventState.equals(AdminUpdateEventState.REJECT_EVENT))
                event.setState(EventState.CANCELED);
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoriesService.getCategory(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        LocationDto updatedLocationDto = updateEventAdminRequest.getLocation();
        if (updatedLocationDto != null) {
            Location updatedLocation = locationsService.getLocationByLatAndLon(updatedLocationDto)
                    .orElse(locationsService.addLocation(updatedLocationDto));
            event.setLocation(updatedLocation);
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        Map<Long, Integer> views = getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = userRequestsService.getConfirmedRequests(List.of(event));
        event = eventsRepository.save(event);

        log.info("Обновлено событие {}", eventId);

        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public Collection<EventShortDto> getEvents(String text,
                                               Collection<Long> categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size) throws ValidationException {
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, FORMATTER);
        checkDateValidity(start, end);

        PagingParametersChecker.check(from, size);
        if ("EVENT_DATE".equals(sort)) sort = "eventDate";
        Pageable pageable = sort == null ? PageRequest.of(from / size, size) : PageRequest.of(from / size, size, Sort.by(sort).descending());

        Collection<Event> events = eventsRepository.getEvents(text.toLowerCase(), categories, paid, start, end, onlyAvailable, pageable).toList();
        return getEventShortDtos(events);
    }

    public EventFullDto getEvent(Long id) {
        Event event = eventsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + id + " не найдено"));

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new EntityNotFoundException("Событие " + id + " не опубликовано.");
        }

        Map<Long, Integer> views = getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = userRequestsService.getConfirmedRequests(List.of(event));


        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public Collection<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        adminUsersService.getUser(userId);

        PagingParametersChecker.check(from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        Collection<Event> events = eventsRepository.findAllByInitiatorId(userId, pageable).toList();
        return getEventShortDtos(events);
    }

    public Event addUserEvent(Long userId, NewEventDto newEventDto) {
        User user = adminUsersService.getUser(userId);
        Category category = categoriesService.getCategory(newEventDto.getCategory());
        Location location = locationsService.addLocation(newEventDto.getLocation());

        Event event = eventsRepository.save(EventMapper.toEvent(newEventDto, user, category, location));
        log.info("Сохранено событие {}", event.getId());

        return event;
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено."));

        Map<Long, Integer> views = getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = userRequestsService.getConfirmedRequests(List.of(event));

        log.info("Запрошено событие {}", eventId);

        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        adminUsersService.getUser(userId);

        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EditingErrorException("Пользователь " + userId + " не является владельцем события " + eventId);
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EditingErrorException("Изменить можно только отмененные события или события в состоянии ожидания модерации.");
        }

        LocalDateTime updatedEventDate = updateEventUserRequest.getEventDate();
        if (updatedEventDate != null) {
            if (updatedEventDate.isBefore(LocalDateTime.now().plusHours(2)))
                throw new EditingErrorException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента.");

            event.setEventDate(updatedEventDate);
        }

        UserUpdateEventState updatedUserEventState = updateEventUserRequest.getStateAction();
        if (updatedUserEventState != null) {
            if (updatedUserEventState.equals(UserUpdateEventState.SEND_TO_REVIEW))
                event.setState(EventState.PENDING);
            if (updatedUserEventState.equals(UserUpdateEventState.CANCEL_REVIEW))
                event.setState(EventState.CANCELED);
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoriesService.getCategory(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        LocationDto updatedLocationDto = updateEventUserRequest.getLocation();
        if (updatedLocationDto != null) {
            Location updatedLocation = locationsService.getLocationByLatAndLon(updatedLocationDto)
                    .orElse(locationsService.addLocation(updatedLocationDto));
            event.setLocation(updatedLocation);
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        Map<Long, Integer> views = getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = userRequestsService.getConfirmedRequests(List.of(event));
        event = eventsRepository.save(event);

        log.info("Обновлено событие {}", eventId);

        return EventMapper.toEventFullDto(
                event,
                views.get(event.getId()),
                confirmedRequests.get(event.getId())
        );
    }

    public Collection<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        return userRequestsService.getUserEventRequests(userId, eventId);
    }

    public EventRequestStatusUpdateResult updateUserEventRequest(Long userId,
                                                                 Long eventId,
                                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return userRequestsService.updateUserEventRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }

    public Collection<EventShortDto> getEventShortDtos(Collection<Event> events) {
        Map<Long, Integer> views = getViews(events);
        Map<Long, Integer> confirmedRequests = userRequestsService.getConfirmedRequests(events);

        Collection<EventShortDto> eventShortDtos = new ArrayList<>();

        for (Event event : events) {
            CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory());
            UserShortDto userShortDto = UserMapper.toUserShortDto(event.getInitiator());
            EventShortDto eventShortDto = EventMapper.toEventShortDto(
                    event,
                    categoryDto,
                    userShortDto,
                    views.get(event.getId()),
                    confirmedRequests.get(event.getId())
            );

            eventShortDtos.add(eventShortDto);
        }

        log.info("Запрошено {} событий", eventShortDtos.size());

        return eventShortDtos;
    }

    private Map<Long, Integer> getViews(Collection<Event> events) {
        Map<Long, Integer> views = new HashMap<>();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        Collection<String> uris = new ArrayList<>();

        for (Event event : events) {
            LocalDateTime publishedOn = event.getPublishedOn();

            if (publishedOn == null) continue;
            if (publishedOn.isBefore(start)) start = publishedOn;
            uris.add("/events/" + event.getId());
        }

        ResponseEntity<Object> response = hitClient.getViewStats(
                start.format(FORMATTER),
                end.format(FORMATTER),
                uris,
                false
        );

        try {
            Collection<ViewStatsDto> viewStatsDtos = List.of(objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));

            for (ViewStatsDto viewStatsDto : viewStatsDtos) {
                Long id = Long.parseLong(viewStatsDto.getUri().split("/")[2]);
                views.put(id, viewStatsDto.getHits());
            }

            return views;
        } catch (JsonProcessingException ignored) {
            return views;
        }
    }

    private void checkDateValidity(LocalDateTime start, LocalDateTime end) throws ValidationException {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }
    }
}
