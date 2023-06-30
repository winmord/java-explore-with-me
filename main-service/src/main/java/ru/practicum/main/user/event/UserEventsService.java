package ru.practicum.main.user.event;

import org.springframework.stereotype.Service;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;

@Service
public class UserEventsService {
    private final EventsRepository eventsRepository;

    public UserEventsService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    public Collection<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        return null;
    }

    public EventFullDto addUserEvent(Long userId, NewEventDto newEventDto) {
        return null;
    }

    public Collection<EventFullDto> getUserEvent(Long userId, Long eventId) {
        return null;
    }

    public Collection<EventFullDto> updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        return null;
    }

    public Collection<ParticipationRequestDto> getUserEventRequest(Long userId, Long eventId) {
        return null;
    }

    public Collection<ParticipationRequestDto> updateUserEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return null;
    }
}
