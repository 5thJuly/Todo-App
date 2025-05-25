package com.app.todoapp.service.serviceimpl;

import com.app.todoapp.dto.CategoryDTO;
import com.app.todoapp.dto.CreateCategoryRequest;
import com.app.todoapp.entities.Categories;
import com.app.todoapp.entities.Todo;
import com.app.todoapp.entities.Users;
import com.app.todoapp.repository.CategoryRepository;
import com.app.todoapp.repository.TodoRepository;
import com.app.todoapp.repository.UserRepository;
import com.app.todoapp.service.interfaces.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Override
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        Users user = userRepository.findByUserId(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + request.getUserId());
        }
        if (categoryRepository.existsByCategoryNameAndUserId(request.getCategoryName(), request.getUserId())) {
            throw new IllegalArgumentException("Category already exists for this user");
        }

        Categories categories = new Categories();
        categories.setCategoryName(request.getCategoryName());
        categories.setUser(user);

        Categories savedCategories = categoryRepository.save(categories);
        return convertToDTO(savedCategories);

    }

    @Override
    public List<CategoryDTO> getCategoriesByUserId(Integer userId) {
        List<Categories> categories = categoryRepository.findByUserUserId(userId);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO updateCategory(Integer categoryId, String categoryName) {
        Categories categories = categoryRepository.findByCategoryId(categoryId);
        if (categories == null) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }
        if (categoryRepository.existsByCategoryNameAndUserId(categoryName, categories.getUser().getUserId())) {
            throw new IllegalArgumentException("Category already exists for this user");
        }
        categories.setCategoryName(categoryName);
        Categories updatedCategories = categoryRepository.save(categories);
        return convertToDTO(updatedCategories);
    }

    @Override
    public void deleteCategory(Integer categoryId, boolean deleteWithTodos) {
        Categories categories = categoryRepository.findByCategoryId(categoryId);
        if (categories == null) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }

        List<Todo> todoInCategory = todoRepository.findByCategoryCategoryId(categoryId);

        if(!todoInCategory.isEmpty() && !deleteWithTodos) {
            throw new IllegalArgumentException("Category has todos, cannot delete without deleting todos");
        }

        if (deleteWithTodos) {
            todoRepository.deleteAll(todoInCategory);
        }
        categoryRepository.delete(categories);

    }

    private CategoryDTO convertToDTO(Categories category) {
        return new CategoryDTO(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getUser().getUserId(),
                category.getCreatedAt()
        );
    }
}
