package ru.practicum.service.hit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.common_dto.EndpointHitDto;

@Service
@Slf4j
public class EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Autowired
    public EndpointHitService(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("Сохранена информация о запросе {}", endpointHit.getId());
    }
}
