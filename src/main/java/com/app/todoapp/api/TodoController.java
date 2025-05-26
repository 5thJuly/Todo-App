package com.app.todoapp.api;

import com.app.todoapp.dto.CreateTodoRequest;
import com.app.todoapp.dto.TodoDTO;
import com.app.todoapp.dto.UpdateTodoRequest;
import com.app.todoapp.response.ApiResponse;
import com.app.todoapp.service.interfaces.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTodo(@RequestBody CreateTodoRequest request) {
        try {
            TodoDTO createdTodo = todoService.createTodo(request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Todo created successfully")
                    .data(createdTodo)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> addTodoToCategory(@PathVariable Integer categoryId,
                                                         @RequestBody CreateTodoRequest request) {
        try {
            TodoDTO createdTodo = todoService.addTodoToCategory(categoryId, request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Todo added to category successfully")
                    .data(createdTodo)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getTodosByUser(@PathVariable Integer userId) {
        try {
            List<TodoDTO> todos = todoService.getTodosByUserId(userId);
            if (todos.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No todos found for this user!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Get All Todos")
                    .data(todos)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getTodosByCategory(@PathVariable Integer categoryId) {
        try {
            List<TodoDTO> todos = todoService.getTodosByCategory(categoryId);
            if (todos.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No todos found for this category!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Get All Todos by Category")
                    .data(todos)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<ApiResponse> getTodosByUserAndCategory(@PathVariable Integer userId,
                                                                 @PathVariable Integer categoryId) {
        try {
            List<TodoDTO> todos = todoService.getTodosByUserAndCategory(userId, categoryId);
            if (todos.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No todos found for this user and category!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Get All Todos by User and Category")
                    .data(todos)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<ApiResponse> getCompletedTodos(@PathVariable Integer userId,
                                                         @RequestParam(defaultValue = "true") boolean completed) {
        try {
            List<TodoDTO> todos = todoService.getTodosByCompletionStatus(userId, completed);
            if (todos.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No todos found with the specified completion status!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Get Todos by Completion Status")
                    .data(todos)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/user/{userId}/due-date")
    public ResponseEntity<ApiResponse> getTodosByDueDate(@PathVariable Integer userId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        try {
            List<TodoDTO> todos = todoService.getTodosByDueDate(userId, dueDate);
            if (todos.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No todos found for this due date!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Get Todos by Due Date")
                    .data(todos)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/date-range/{userId}")
    public ResponseEntity<ApiResponse> getTodosByDateRange(@PathVariable Integer userId,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<TodoDTO> todos = todoService.getTodosByDateRange(userId, startDate, endDate);
            if (todos.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No todos found in this date range!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Get Todos by Date Range")
                    .data(todos)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/search/{userId}")
    public ResponseEntity<ApiResponse> searchTodos(@PathVariable Integer userId,
                                                   @RequestParam String keyword) {
        try {
            List<TodoDTO> todos = todoService.searchTodos(userId, keyword);
            if (todos.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(false)
                        .message("No todos found for this search keyword!")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Search Todos")
                    .data(todos)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PatchMapping("/update/{todoId}")
    public ResponseEntity<ApiResponse> updateTodo(@PathVariable Integer todoId,
                                                  @RequestBody UpdateTodoRequest request) {
        try {
            TodoDTO updatedTodo = todoService.updateTodo(todoId, request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Todo updated successfully")
                    .data(updatedTodo)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PatchMapping("/completed/{todoId}")
    public ResponseEntity<ApiResponse> markAsCompleted(@PathVariable Integer todoId,
                                                       @RequestParam boolean completed) {
        try {
            TodoDTO updatedTodo = todoService.markAsCompleted(todoId, completed);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Todo completion status updated")
                    .data(updatedTodo)
                    .build());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/delete/{todoId}")
    public ResponseEntity<ApiResponse> deleteTodo(@PathVariable Integer todoId) {
        try {
            todoService.deleteTodo(todoId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Todo deleted successfully")
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
