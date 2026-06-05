package edu.ptithcm.learnnextbackend.modules.category;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.category.dto.response.CategoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategories()));
    }
}
