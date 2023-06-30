package ru.practicum.main.event.model;

import lombok.*;
import ru.practicum.main.user.model.User;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.enums.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENTS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ANNOTATION")
    private String annotation;

    @OneToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Column(name = "CONFIRMED_REQUESTS")
    private Integer confirmedRequests;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "EVENT_DATE")
    private LocalDateTime eventDate;

    @OneToOne
    @JoinColumn(name = "INITIATOR_ID")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    @Column(name = "PAID")
    private Boolean paid;

    @Column(name = "PARTICIPANT_LIMIT")
    private Integer participantLimit;

    @Column(name = "PUBLISHED_ON")
    private LocalDateTime publishedOn;

    @Column(name = "REQUEST_MODERATION")
    private Boolean requestModeration;

    @Column(name = "STATE")
    private EventState state;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "VIEWS")
    private Integer views;
}