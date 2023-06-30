package ru.practicum.main.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.service.UserRequestsService;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;

@RestController
@RequestMapping("/user/{userId}/requests")
public class UserRequestsController {
    private final UserRequestsService userRequestsService;

    public UserRequestsController(UserRequestsService userRequestsService) {
        this.userRequestsService = userRequestsService;
    }

    @GetMapping
    public Collection<ParticipationRequestDto> getRequests(@PathVariable Integer userId) {
        return userRequestsService.getRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto addRequest(@PathVariable Integer userId,
                                              @RequestParam Integer eventId) {
        return userRequestsService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public void cancelRequest(@PathVariable Integer userId,
                              @RequestParam Integer requestId) {
        userRequestsService.cancelRequest(userId, requestId);
    }
}
