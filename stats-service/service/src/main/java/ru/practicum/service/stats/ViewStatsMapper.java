package ru.practicum.service.stats;

import ru.practicum.common_dto.ViewStatsDto;

public class ViewStatsMapper {
    private ViewStatsMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ViewStatsDto toDto(ViewStatsShort viewStatsShort, Integer hits) {
        return ViewStatsDto.builder()
                .app(viewStatsShort.getApp())
                .uri(viewStatsShort.getUri())
                .hits(hits)
                .build();
    }
}
