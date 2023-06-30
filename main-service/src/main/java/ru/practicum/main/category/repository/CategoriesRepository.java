package ru.practicum.main.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.model.Category;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
}
