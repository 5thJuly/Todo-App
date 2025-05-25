package com.app.todoapp.service.interfaces;

import com.app.todoapp.dto.CreateTodoRequest;
import com.app.todoapp.dto.TodoDTO;
import com.app.todoapp.dto.UpdateTodoRequest;

import java.time.LocalDate;
import java.util.List;

public interface TodoService {

    TodoDTO createTodo(CreateTodoRequest request);

    TodoDTO addTodoToCategory(Integer categoryId, CreateTodoRequest request);

    List<TodoDTO> getTodosByUserId(Integer userId);

    List<TodoDTO> getTodosByCategory(Integer categoryId);

    List<TodoDTO> getTodosByUserAndCategory(Integer userId, Integer categoryId);

    List<TodoDTO> getTodosByCompletionStatus(Integer userId, boolean completed);

    List<TodoDTO> getTodosByDueDate(Integer userId, LocalDate dueDate);

    List<TodoDTO> getTodosByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

    List<TodoDTO> searchTodos(Integer userId, String keyword);

    TodoDTO updateTodo(Integer todoId, UpdateTodoRequest request);

    TodoDTO markAsCompleted(Integer todoId, boolean completed);

    void deleteTodo(Integer todoId);
}
