package ru.practicum.service.hit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.common_dto.EndpointHitDto;

@Service
public class EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Autowired
    public EndpointHitService(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    public EndpointHit saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        return endpointHitRepository.save(endpointHit);
    }
}
