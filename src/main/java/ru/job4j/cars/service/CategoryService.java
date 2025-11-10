package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Category;
import ru.job4j.cars.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(Category category) {
        return categoryRepository.create(category);
    }

    public void update(Category category) {
        categoryRepository.update(category);
    }

    public void delete(int categoryId) {
        categoryRepository.delete(categoryId);
    }

    public List<Category> findAllOrderById() {
        return categoryRepository.findAllOrderById();
    }

    public Optional<Category> findById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }
}
