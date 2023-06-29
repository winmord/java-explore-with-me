package ru.practicum.main.category;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
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

    public CategoryDto getCategory(@RequestParam(name = "catId") Integer catId) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));

        return CategoryMapper.toCategoryDto(category);
    }
}
