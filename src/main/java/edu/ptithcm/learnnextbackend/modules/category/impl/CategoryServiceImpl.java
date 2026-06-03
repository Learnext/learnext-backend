package edu.ptithcm.learnnextbackend.modules.category.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.category.CategoryRepository;
import edu.ptithcm.learnnextbackend.modules.category.CategoryService;
import edu.ptithcm.learnnextbackend.modules.category.dto.request.CreateCategoryRequest;
import edu.ptithcm.learnnextbackend.modules.category.dto.response.CategoryResponse;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.Normalizer;
import java.util.Locale;

public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(CreateCategoryRequest req) {
        if (categoryRepository.existsByNameIgnoreCase(req.getName())) {
            throw new BadRequestException("Category already exists");
        }

        String slug = toSlug(req.getName());

        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category slug already exists");
        }

        Category category = Category.builder()
                .nameCategory(req.getName())
                .slug(slug)
                .description(req.getDescription())
                .active(true)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getNameCategory())
                .slug(category.getSlug())
                .description(category.getDescription())
                .active(category.isActive())
                .build();
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}
