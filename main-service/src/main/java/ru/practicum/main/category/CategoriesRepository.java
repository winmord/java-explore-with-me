package ru.practicum.main.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
}
