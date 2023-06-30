package ru.practicum.main.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoriesRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;

    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public Collection<CategoryDto> getCategories() {
        return categoriesRepository.findAll()
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public Category getCategory(@RequestParam(name = "catId") Integer catId) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
        log.info("Запрошена категория {}", catId);

        return category;
    }
}
