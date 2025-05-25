package com.app.todoapp.repository;

import com.app.todoapp.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findByUserUserId(Integer userId);
    List<Todo> findByCategoryCategoryId(Integer categoryId);

    @Query("SELECT t FROM Todo t WHERE t.user.userId = :userId AND t.dueDate = :dueDate")
    List<Todo> findByUserIdAndDueDate(@Param("userId") Integer userId, @Param("dueDate") LocalDate dueDate);

    @Query("SELECT t FROM Todo t WHERE t.user.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate")
    List<Todo> findByUserIdAndDueDateBetween(@Param("userId") Integer userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Todo t WHERE t.user.userId = :userId AND (t.title LIKE %:keyword% OR t.description LIKE %:keyword%)")
    List<Todo> findByUserIdAndKeyword(@Param("userId") Integer userId, @Param("keyword") String keyword);

    List<Todo> findByUserUserIdAndCategoryCategoryId(Integer userId, Integer categoryId);

    List<Todo> findByUserUserIdAndIsCompleted(Integer userId, boolean isCompleted);

}
