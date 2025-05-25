package com.app.todoapp.service.interfaces;

import com.app.todoapp.dto.CategoryDTO;
import com.app.todoapp.dto.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CreateCategoryRequest request);

    List<CategoryDTO> getCategoriesByUserId(Integer userId);

    CategoryDTO updateCategory(Integer categoryId, String categoryName);

    void deleteCategory(Integer categoryId, boolean deleteWithTodos);


}
