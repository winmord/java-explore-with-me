package ru.practicum.main.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventsRepository;

import java.util.Collection;

@Service
public class EventsService {
    private final EventsRepository eventsRepository;

    public EventsService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    public Collection<EventShortDto> getEvents(String text,
                                               Collection<Integer> categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size) {
        return eventsRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    public Event getEvent(Long id) {
        return eventsRepository.findById(id).orElseThrow();
    }
}
