package edu.ptithcm.learnnextbackend.modules.category.mapper;

import edu.ptithcm.learnnextbackend.modules.category.dto.response.CategoryResponse;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;

public final class CategoryMapper {
    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
