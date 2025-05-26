package com.app.todoapp.api;

import com.app.todoapp.dto.CategoryDTO;
import com.app.todoapp.dto.CreateCategoryRequest;
import com.app.todoapp.response.ApiResponse;
import com.app.todoapp.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCategory(@RequestBody CreateCategoryRequest request) {
        try {
            CategoryDTO createdCategory = categoryService.createCategory(request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Category created successfully")
                    .data(createdCategory)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/getCategories/{userId}")
    public ResponseEntity<ApiResponse> getCategoriesByUser(@PathVariable Integer userId) {
        try {
            List<CategoryDTO> categories = categoryService.getCategoriesByUserId(userId);
            if (categories.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No categories found for this user!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Get All Categories")
                    .data(categories)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PatchMapping("update/{categoryId}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Integer categoryId,
                                                      @RequestBody String categoryName) {
        try {
            CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryName);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Category updated successfully")
                    .data(updatedCategory)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer categoryId,
                                                      @RequestParam(defaultValue = "false") boolean deleteWithTodos) {
        try {
            categoryService.deleteCategory(categoryId, deleteWithTodos);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Category deleted successfully")
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<ApiResponse> handleException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } else if (e instanceof RuntimeException) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Internal Server Error: " + e.getMessage())
                            .build());
        }
    }
}