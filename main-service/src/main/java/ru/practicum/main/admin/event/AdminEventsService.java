package ru.practicum.main.admin.event;

import org.springframework.stereotype.Service;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.service.CategoriesService;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventsRepository;

import java.util.Collection;

@Service
public class AdminEventsService {
    private final EventsRepository eventsRepository;
    private final CategoriesService categoriesService;

    public AdminEventsService(EventsRepository eventsRepository, CategoriesService categoriesService) {
        this.eventsRepository = eventsRepository;
        this.categoriesService = categoriesService;
    }

    public Collection<EventFullDto> getEvents(Collection<Integer> users,
                                              Collection<String> states,
                                              Collection<Integer> categories,
                                              String rangeStart,
                                              String rangeEnd,
                                              Integer from,
                                              Integer size) {
        return eventsRepository.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    public Event updateEvent(Long eventId,
                                    UpdateEventAdminRequest updateEventAdminRequest) {
        Category category = categoriesService.getCategory(updateEventAdminRequest.getCategory());
        Event savedEvent = eventsRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Событие " + eventId + " не найдено"));
        Event event = EventMapper.toEvent(updateEventAdminRequest, category);
        event.setId(savedEvent.getId());

        return eventsRepository.save(event);
    }
}
