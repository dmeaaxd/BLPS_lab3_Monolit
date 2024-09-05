package org.example.blps_lab3_monolit.app.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.category.CategoryDTORequest;
import org.example.blps_lab3_monolit.app.dto.category.CategoryDTOResponse;
import org.example.blps_lab3_monolit.app.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {

    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTOResponse>> getAll() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoryDTORequest categoryDTORequest) {

        Map<String, String> response = new HashMap<>();

        if (categoryDTORequest.antiChecker()) {
            response.put("error", "Переданы неверные параметры в запросе");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            return new ResponseEntity<>(categoryService.create(categoryDTORequest), HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Категория с данным названием уже есть: " + categoryDTORequest.getName());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody CategoryDTORequest categoryDTORequest) {

        Map<String, String> response = new HashMap<>();

        if (categoryDTORequest.antiChecker()) {
            response.put("error", "Переданы неверные параметры в запросе");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            return new ResponseEntity<>(categoryService.update(id, categoryDTORequest), HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        try {
            categoryService.delete(id);
            response.put("message", "Category deleted");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", "Категория с данным ID не существует: " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
