package ru.practicum.service.stats;

import org.springframework.stereotype.Service;
import ru.practicum.common_dto.ViewStatsDto;
import ru.practicum.service.hit.EndpointHitRepository;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;

@Service
public class ViewStatsService {
    private final EndpointHitRepository endpointHitRepository;

    public ViewStatsService(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    public Collection<ViewStatsDto> getStats(String start,
                                             String end,
                                             Collection<String> uris,
                                             boolean unique) throws ValidationException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        checkDateValidity(startDate, endDate);

        if (uris == null) uris = Collections.emptyList();
        if (unique) return endpointHitRepository.getViewStatsUnique(startDate, endDate, uris);

        return endpointHitRepository.getViewStats(startDate, endDate, uris);
    }

    private void checkDateValidity(LocalDateTime start, LocalDateTime end) throws ValidationException {
        if (start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }
    }
}
