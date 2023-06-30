package ru.practicum.main.event.mapper;

import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.mapper.UserMapper;

public class EventMapper {
    private EventMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Event toEvent(UpdateEventAdminRequest updateEventAdminRequest, Category category) {
        return Event.builder()
                .annotation(updateEventAdminRequest.getAnnotation())
                .category(category)
                .description(updateEventAdminRequest.getDescription())
                .eventDate(updateEventAdminRequest.getEventDate())
                .paid(updateEventAdminRequest.getPaid())
                .participantLimit(updateEventAdminRequest.getParticipantLimit())
                .requestModeration(updateEventAdminRequest.getRequestModeration())
                // TODO state
                .title(updateEventAdminRequest.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .build();
    }
}
