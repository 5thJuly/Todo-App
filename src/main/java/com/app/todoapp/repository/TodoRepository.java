package com.app.todoapp.repository;

import com.app.todoapp.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findByUserUserId(Integer userId);
    List<Todo> findByCategoryCategoryId(Integer categoryId);
    List<Todo> findByCompleted(boolean completed);
}
