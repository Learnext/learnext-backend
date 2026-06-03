package edu.ptithcm.learnnextbackend.modules.category;

import edu.ptithcm.learnnextbackend.modules.category.dto.request.CreateCategoryRequest;
import edu.ptithcm.learnnextbackend.modules.category.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CreateCategoryRequest request);

}
