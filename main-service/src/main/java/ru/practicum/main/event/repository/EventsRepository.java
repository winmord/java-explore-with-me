package ru.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.dto.EventFullDto;

import java.util.Collection;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {
    Collection<EventFullDto> getEvents(Collection<Integer> users,
                                              Collection<String> states,
                                              Collection<Integer> categories,
                                              String rangeStart,
                                              String rangeEnd,
                                              Integer from,
                                              Integer size);

    Collection<EventShortDto> getEvents(String text,
                                        Collection<Integer> categories,
                                        Boolean paid,
                                        String rangeStart,
                                        String rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        Integer from,
                                        Integer size);
}
