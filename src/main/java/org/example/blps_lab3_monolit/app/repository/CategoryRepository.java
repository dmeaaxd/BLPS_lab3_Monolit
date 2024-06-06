package org.example.blps_lab3_monolit.app.repository;

import org.example.blps_lab3_monolit.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
