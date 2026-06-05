package edu.ptithcm.learnnextbackend.modules.category;

import edu.ptithcm.learnnextbackend.modules.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();
}
