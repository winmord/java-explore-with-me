package ru.practicum.main.compilation.model;

import lombok.*;
import ru.practicum.main.event.model.Event;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "COMPILATIONS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PINNED")
    private Boolean pinned;

    @Column(name = "TITLE")
    private String title;

    @OneToMany
    private Collection<Event> events;
}
