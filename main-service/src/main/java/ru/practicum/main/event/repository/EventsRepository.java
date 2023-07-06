package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("select ev " +
            "from Event as ev " +
            "where (:users is null or ev.initiator.id in :users) " +
            "and (:states is null or ev.state in :states) " +
            "and (:categories is null or ev.category.id in :categories) " +
            "and (:start is null or ev.eventDate > :start) " +
            "and (:end is null or ev.eventDate < :end)")
    Page<Event> getEvents(Collection<Long> users,
                          Collection<EventState> states,
                          Collection<Long> categories,
                          LocalDateTime start,
                          LocalDateTime end,
                          Pageable pageable);

    @Query("select ev from " +
            "Event as ev " +
            "where (:text is null) or ((lower(ev.annotation) like '%'+lower(:text)+'%') or (lower(ev.description) like '%'+lower(:text)+'%')) " +
            "and (:categories is null or ev.category.id in :categories) " +
            "and (:paid is null or ev.paid = :paid) " +
            "and (:start is null or ev.eventDate > :start) " +
            "and (:end is null or ev.eventDate < :end) " +
            "and (:onlyAvailable is null or (true = :onlyAvailable and ev.participantLimit > 0)) " +
            "and true = :onlyAvailable")
    Page<Event> getEvents(String text,
                          Collection<Long> categories,
                          Boolean paid,
                          LocalDateTime start,
                          LocalDateTime end,
                          Boolean onlyAvailable,
                          Pageable pageable);
}
