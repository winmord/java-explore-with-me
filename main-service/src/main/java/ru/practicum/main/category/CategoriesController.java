package ru.practicum.main.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public Collection<CategoryDto> getCategories() {
        return categoriesService.getCategories();
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@RequestParam(name = "catId") Integer catId) {
        return categoriesService.getCategory(catId);
    }
}
