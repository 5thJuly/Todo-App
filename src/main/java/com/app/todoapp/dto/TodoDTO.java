package com.app.todoapp.dto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDTO {
    private Integer todoId;
    private String title;
    private String description;
    private Boolean isCompleted;
    private LocalDate dueDate;
    private Integer userId;
    private Integer categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
