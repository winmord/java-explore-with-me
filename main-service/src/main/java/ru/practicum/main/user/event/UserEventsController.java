package ru.practicum.main.user.event;

import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;

@RestController
@RequestMapping("/user/{userId}/events")
public class UserEventsController {
    private final UserEventsService userEventsService;

    public UserEventsController(UserEventsService userEventsService) {
        this.userEventsService = userEventsService;
    }

    @GetMapping
    public Collection<EventShortDto> getUserEvents(@PathVariable(name = "userId") Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return userEventsService.getUserEvents(userId, from, size);
    }

    @PostMapping
    public EventFullDto addUserEvent(@PathVariable(name = "userId") Long userId,
                                     @RequestBody NewEventDto newEventDto) {
        return userEventsService.addUserEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public Collection<EventFullDto> getUserEvent(@PathVariable(name = "userId") Long userId,
                                                 @PathVariable(name = "eventId") Long eventId) {
        return userEventsService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public Collection<EventFullDto> updateUserEvent(@PathVariable(name = "userId") Long userId,
                                                    @PathVariable(name = "eventId") Long eventId,
                                                    @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return userEventsService.updateUserEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getUserEventRequest(@PathVariable(name = "userId") Long userId,
                                                                   @PathVariable(name = "eventId") Long eventId) {
        return userEventsService.getUserEventRequest(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> updateUserEventRequest(@PathVariable(name = "userId") Long userId,
                                                                      @PathVariable(name = "eventId") Long eventId,
                                                                      @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return userEventsService.updateUserEventRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
