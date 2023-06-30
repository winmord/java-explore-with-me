package ru.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.dto.LocationDto;

@Repository
public interface LocationsRepository extends JpaRepository<LocationDto, Long> {
}
