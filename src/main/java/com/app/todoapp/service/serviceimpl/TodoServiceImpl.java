package com.app.todoapp.service.serviceimpl;

import com.app.todoapp.dto.CreateTodoRequest;
import com.app.todoapp.dto.TodoDTO;
import com.app.todoapp.dto.UpdateTodoRequest;
import com.app.todoapp.entities.Categories;
import com.app.todoapp.entities.Todo;
import com.app.todoapp.entities.Users;
import com.app.todoapp.repository.CategoryRepository;
import com.app.todoapp.repository.TodoRepository;
import com.app.todoapp.repository.UserRepository;
import com.app.todoapp.service.interfaces.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public TodoDTO createTodo(CreateTodoRequest request) {
        Users user = userRepository.findByUserId(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + request.getUserId());
        }

        Categories categories = categoryRepository.findByCategoryId(request.getCategoryId());
        if (categories == null) {
            throw new IllegalArgumentException("Category not found with ID: " + request.getCategoryId());
        }
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());
        todo.setUser(user);
        todo.setCategory(categories);
        todo.setIsCompleted(false);

        Todo savedTodo = todoRepository.save(todo);
        return convertToDTO(savedTodo);
    }

    @Override
    public TodoDTO addTodoToCategory(Integer categoryId, CreateTodoRequest request) {
        Categories categories = categoryRepository.findByCategoryId(categoryId);
        if (categories == null) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }

        Users user = userRepository.findByUserId(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + request.getUserId());
        }

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());
        todo.setUser(user);
        todo.setCategory(categories);
        todo.setIsCompleted(false);

        Todo savedTodo = todoRepository.save(todo);
        return convertToDTO(savedTodo);
    }

    @Override
    public List<TodoDTO> getTodosByUserId(Integer userId) {
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserUserId(userId);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> getTodosByCategory(Integer categoryId) {
        Categories category = categoryRepository.findByCategoryId(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }

        List<Todo> todos = todoRepository.findByCategoryCategoryId(categoryId);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> getTodosByUserAndCategory(Integer userId, Integer categoryId) {
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        Categories category = categoryRepository.findByCategoryId(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }

        List<Todo> todos = todoRepository.findByUserUserIdAndCategoryCategoryId(userId, categoryId);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> getTodosByCompletionStatus(Integer userId, boolean completed) {
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserUserIdAndIsCompleted(userId, completed);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> getTodosByDueDate(Integer userId, LocalDate dueDate) {
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserIdAndDueDate(userId, dueDate);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> getTodosByDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserIdAndDueDateBetween(userId, startDate, endDate);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> searchTodos(Integer userId, String keyword) {
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserIdAndKeyword(userId, keyword);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TodoDTO updateTodo(Integer todoId, UpdateTodoRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            todo.setDueDate(request.getDueDate());
        }
        if (request.getCategoryId() != null) {
            Categories category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            todo.setCategory(category);
        }
        if (request.getIsCompleted() != null) {
            todo.setIsCompleted(request.getIsCompleted());
        }

        Todo updatedTodo = todoRepository.save(todo);
        return convertToDTO(updatedTodo);
    }

    @Override
    public TodoDTO markAsCompleted(Integer todoId, boolean completed) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setIsCompleted(completed);
        Todo updatedTodo = todoRepository.save(todo);
        return convertToDTO(updatedTodo);
    }

    @Override
    public void deleteTodo(Integer todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todoRepository.delete(todo);
    }

    private TodoDTO convertToDTO(Todo todo) {
        return new TodoDTO(
                todo.getTodoId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getIsCompleted(),
                todo.getDueDate(),
                todo.getUser().getUserId(),
                todo.getCategory().getCategoryId(),
                todo.getCategory().getCategoryName(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
