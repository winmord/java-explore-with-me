package ru.practicum.main.category;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "CATEGORIES")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME", unique = true)
    private String name;
}
