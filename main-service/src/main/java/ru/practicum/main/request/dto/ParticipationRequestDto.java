package ru.practicum.main.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private Long id;

    private Integer event;
    private Integer requester;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:ss.SSS")
    private LocalDateTime created;
}
