package ru.practicum.main.admin.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.mapper.EventMapper;

import java.util.Collection;

@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {
    private final AdminEventsService adminEventsService;

    @Autowired
    public AdminEventsController(AdminEventsService adminEventsService) {
        this.adminEventsService = adminEventsService;
    }

    @GetMapping
    public Collection<EventFullDto> getEvents(@RequestParam Collection<Integer> users,
                                              @RequestParam Collection<String> states,
                                              @RequestParam Collection<Integer> categories,
                                              @RequestParam String rangeStart,
                                              @RequestParam String rangeEnd,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return adminEventsService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return EventMapper.toEventFullDto(adminEventsService.updateEvent(eventId, updateEventAdminRequest));
    }
}
