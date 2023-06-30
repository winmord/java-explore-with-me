package ru.practicum.main.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;

@Service
public class UserRequestsService {
    public Collection<ParticipationRequestDto> getRequests(Integer userId) {
        return null;
    }

    public ParticipationRequestDto addRequest(Integer userId,
                                              Integer eventId) {
        return null;
    }

    public void cancelRequest(Integer userId,
                              Integer requestId) {
    }
}
