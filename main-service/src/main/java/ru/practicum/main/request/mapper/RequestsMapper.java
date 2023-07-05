package ru.practicum.main.request.mapper;

import ru.practicum.main.event.model.Event;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.user.model.User;

public class RequestsMapper {
    private RequestsMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .created(request.getCreated())
                .build();
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto, User requester, Event event) {
        return Request.builder()
                .requester(requester)
                .event(event)
                .status(RequestStatus.valueOf(participationRequestDto.getStatus()))
                .build();
    }
}
