package org.example.blps_lab3_monolit.app.service;


import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.category.CategoryDTORequest;
import org.example.blps_lab3_monolit.app.dto.category.CategoryDTOResponse;
import org.example.blps_lab3_monolit.app.entity.Category;
import org.example.blps_lab3_monolit.app.repository.CategoryRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDTOResponse create(CategoryDTORequest categoryDTORequest) throws Exception {
        Category newCategory = Category.builder()
                .name(categoryDTORequest.getName())
                .build();

        if (categoryRepository.findByName(newCategory.getName()).isPresent()) {
            throw new Exception("Эта категория уже есть");
        }

        newCategory = categoryRepository.save(newCategory);

        return CategoryDTOResponse.builder()
                .id(newCategory.getId())
                .name(newCategory.getName())
                .build();
    }


    public CategoryDTOResponse update(Long id, CategoryDTORequest categoryDTORequest) throws Exception {
        Optional<Category> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isEmpty()) {
            throw new Exception("Категория с данным ID не найдена: " + id);
        }

        Category category = optionalCategory.get();
        String newName = categoryDTORequest.getName();

        if (newName != null && !newName.equals(category.getName())) {
            if (categoryRepository.findByName(newName).isPresent()) {
                throw new Exception("Категория с данным наименование уже существует: " + id);
            }
            category.setName(newName);
        }

        Category updatedCategory = categoryRepository.save(category);

        return CategoryDTOResponse.builder()
                .id(updatedCategory.getId())
                .name(updatedCategory.getName())
                .build();
    }


    public Long delete(Long id) throws Exception {
        Optional<Category> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isEmpty()) {
            throw new Exception("Категория не найдена");
        }

        Category category = optionalCategory.get();
        categoryRepository.delete(category);

        return category.getId();
    }

    public List<CategoryDTOResponse> getAll() {

        List<CategoryDTOResponse> CategoryDTOResponseList = new ArrayList<>();

        for (Category category : categoryRepository.findAll()) {
            CategoryDTOResponseList.add(CategoryDTOResponse.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .build());
        }
        return CategoryDTOResponseList;
    }
}
