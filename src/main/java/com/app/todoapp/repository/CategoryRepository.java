package com.app.todoapp.repository;

import com.app.todoapp.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, Integer> {

    List<Categories> findByUserUserId(Integer userId);
}
