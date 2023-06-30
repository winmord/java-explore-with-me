package ru.practicum.main.event.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.service.EventsService;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.Collection;

@RestController
@RequestMapping("/events")
public class EventsController {
    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public Collection<EventShortDto> getEvents(@RequestParam String text,
                                               @RequestParam Collection<Integer> categories,
                                               @RequestParam Boolean paid,
                                               @RequestParam String rangeStart,
                                               @RequestParam String rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam String sort,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return eventsService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id) {
        return EventMapper.toEventFullDto(eventsService.getEvent(id));
    }
}
