package com.app.todoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UpdateTodoRequest {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer categoryId;
    private Boolean isCompleted;
}
