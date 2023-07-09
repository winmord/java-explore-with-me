package ru.practicum.main.rating.model;

import lombok.*;
import ru.practicum.main.rating.enums.RatingState;

import javax.persistence.*;

@Entity
@Table(name = "RATINGS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "EVENT_ID")
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE")
    private RatingState state;
}
