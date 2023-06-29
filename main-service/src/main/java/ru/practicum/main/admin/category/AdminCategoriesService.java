package ru.practicum.main.admin.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.CategoriesRepository;
import ru.practicum.main.category.Category;
import ru.practicum.main.category.CategoryDto;
import ru.practicum.main.category.CategoryMapper;

@Service
@Slf4j
public class AdminCategoriesService {
    private final CategoriesRepository categoriesRepository;

    public AdminCategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public CategoryDto addNewCategory(NewCategoryDto categoryDto) {
        // TODO Обработать уникальность имени
        Category category = categoriesRepository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Добавлена категория с id {}", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    public void deleteCategory(Integer catId) {
        // TODO Проверить есть ли зависимые от категории события
        categoriesRepository.deleteById(catId);
        log.info("Удалена категория с id {}", catId);
    }

    public CategoryDto updateCategory(Integer catId,CategoryDto categoryDto) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена или не существует"));

        category.setName(categoryDto.getName());
        Category updatedCategory = categoriesRepository.save(category);
        log.info("Обновлена категория с id {}", catId);

        return CategoryMapper.toCategoryDto(updatedCategory);
    }
}
