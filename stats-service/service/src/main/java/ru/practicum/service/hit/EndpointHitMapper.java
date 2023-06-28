package ru.practicum.service.hit;

import ru.practicum.common_dto.EndpointHitDto;

public class EndpointHitMapper {
    private EndpointHitMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
