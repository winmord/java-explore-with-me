package ru.practicum.main.admin.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.repository.CategoriesRepository;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.dto.NewCategoryDto;

@Service
@Slf4j
public class AdminCategoriesService {
    private final CategoriesRepository categoriesRepository;

    public AdminCategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public CategoryDto addNewCategory(NewCategoryDto categoryDto) {
        Category category = categoriesRepository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Добавлена категория с id {}", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    public void deleteCategory(Long catId) {
        // TODO Проверить есть ли зависимые от категории события
        categoriesRepository.deleteById(catId);
        log.info("Удалена категория с id {}", catId);
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена или не существует"));

        category.setName(categoryDto.getName());
        Category updatedCategory = categoriesRepository.save(category);
        log.info("Обновлена категория с id {}", catId);

        return CategoryMapper.toCategoryDto(updatedCategory);
    }
}
