package edu.ptithcm.learnnextbackend.modules.category.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.category.CategoryRepository;
import edu.ptithcm.learnnextbackend.modules.category.CategoryService;
import edu.ptithcm.learnnextbackend.modules.category.dto.request.CreateCategoryRequest;
import edu.ptithcm.learnnextbackend.modules.category.dto.response.CategoryResponse;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(CreateCategoryRequest req) {
        String slug = toSlug(req.getName());

        if (slug == null || slug.isBlank()) {
            throw new RuntimeException("Category slug is invalid");
        }

        boolean nameExists = categoryRepository.existsByNameIgnoreCase(req.getName());
        boolean slugExists = categoryRepository.existsBySlug(slug);

        if (nameExists || slugExists) {
            throw new RuntimeException("Category name or slug already exists");
        }

        Category category = Category.builder()
                .name(req.getName())
                .slug(slug)
                .description(req.getDescription())
                .active(true)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found"));

        return toResponse(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .active(category.isActive())
                .build();
    }

    private String toSlug(String input) {
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D")
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }
}
