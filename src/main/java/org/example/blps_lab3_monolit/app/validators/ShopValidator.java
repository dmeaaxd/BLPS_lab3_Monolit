package org.example.blps_lab3_monolit.app.validators;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.entity.Category;
import org.example.blps_lab3_monolit.app.entity.Shop;
import org.example.blps_lab3_monolit.app.repository.CategoryRepository;
import org.example.blps_lab3_monolit.app.repository.ShopRepository;
import org.example.blps_lab3_monolit.app.utils.Sort;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ShopValidator {
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;


    public ValidationResult validateGetAllRequest(List<Integer> categories, String sort, Integer page) {

        boolean result;
        String message;

        ValidationResult tmpValidationResult = validateCategories(categories);
        result = tmpValidationResult.isCorrect();
        message = tmpValidationResult.getMessage();

        tmpValidationResult = validateSort(sort);
        if (result) {
            result = tmpValidationResult.isCorrect();
            message = tmpValidationResult.getMessage();
        }

        tmpValidationResult = validatePage(page, categories);
        if (result) {
            result = tmpValidationResult.isCorrect();
            message = tmpValidationResult.getMessage();
        }

        return new ValidationResult(result, message);
    }

    private ValidationResult validateCategories(List<Integer> categories) {
        if (categories == null) {
            return new ValidationResult(true, null);
        }

        try {
            for (int categoryId : categories) {
                categoryRepository.findById((long) categoryId).orElseThrow(() -> new ObjectNotFoundException(categoryId, "Категория"));
            }
        } catch (NumberFormatException numberFormatException) {
            return new ValidationResult(false, "Параметр categories задан неверно");
        } catch (ObjectNotFoundException objectNotFoundException) {
            return new ValidationResult(false, "Категория с идентификатором " + objectNotFoundException.getIdentifier() + " не найдена");
        }
        return new ValidationResult(true, null);
    }

    private ValidationResult validateSort(String sort) {
        if (sort == null) {
            return new ValidationResult(true, null);
        }

        try {
            Sort resultSort = Sort.parseSort(sort);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ValidationResult(false, "Параметр sort задан неверно");
        }

        return new ValidationResult(true, null);
    }

    private ValidationResult validatePage(Integer page, List<Integer> categories) {
        if (page == null) {
            return new ValidationResult(true, null);
        }

        ValidationResult tmpValidationResult = validateCategories(categories);
        if (!tmpValidationResult.isCorrect()) return tmpValidationResult;

        // Фильтруем список магазинов по категориям
        List<Category> allowCategories = new ArrayList<>();
        if (categories != null) {
            for (int categoryId : categories) {
                allowCategories.add(categoryRepository.findById((long) categoryId).orElseThrow(() -> new ObjectNotFoundException(categoryId, "Категория")));
            }
        }

        List<Shop> filteredShops = new ArrayList<>();
        for (Shop shop : shopRepository.findAll()) {
            if (allowCategories.isEmpty() || allowCategories.contains(shop.getCategory())) {
                filteredShops.add(shop);
            }
        }

        // Проверяем, что page в границах
        double shopCount = filteredShops.size();
        int pagesCount = (int) Math.ceil(shopCount / shopRepository.PAGE_SIZE);
        if (pagesCount == 0) pagesCount = 1;

        if ((page > pagesCount) || page < 1) {
            return new ValidationResult(false, HttpStatus.NOT_FOUND.toString());
        }

        return new ValidationResult(true, null);
    }
}
