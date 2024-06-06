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
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {

    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTOResponse>> getAll() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CategoryDTORequest categoryDTORequest) {

        Map<String, String> response = new HashMap<>();

        if (categoryDTORequest.antiChecker()) {
            response.put("error", "Переданы неверные параметры в запросе");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            response.put("categoryId", String.valueOf(categoryService.create(categoryDTORequest)));
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("error", "Категория с данным названием уже есть: " + categoryDTORequest.getName());
            return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PostMapping("/{id}/update")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody CategoryDTORequest categoryDTORequest) {

        Map<String, String> response = new HashMap<>();

        if (categoryDTORequest.antiChecker()) {
            response.put("error", "Переданы неверные параметры в запросе");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            response.put("categoryId", String.valueOf(categoryService.update(id, categoryDTORequest)));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", e.getMessage() + id);
            return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        try {
            response.put("categoryId", String.valueOf(categoryService.delete(id)));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", "Категория с данным ID не существует: " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
