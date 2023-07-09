package ru.practicum.main.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.rating.model.Rating;

import java.util.Collection;
import java.util.Optional;

public interface RatingsRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndEventId(Long userId, Long eventId);

    Collection<Rating> findAllByEventId(Long eventId);
}
