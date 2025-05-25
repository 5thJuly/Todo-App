package com.app.todoapp.repository;

import com.app.todoapp.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, Integer> {

    List<Categories> findByUserUserId(Integer userId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Categories c WHERE c.categoryName = :categoryName AND c.user.userId = :userId")
    boolean existsByCategoryNameAndUserId(@Param("categoryName") String categoryName, @Param("userId") Integer userId);

    Categories findByCategoryId(Integer categoryId);
}
