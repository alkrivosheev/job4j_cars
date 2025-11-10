package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.BrandRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public Brand create(Brand brand) {
        return brandRepository.create(brand);
    }

    public void update(Brand brand) {
        brandRepository.update(brand);
    }

    public void delete(int brandId) {
        brandRepository.delete(brandId);
    }

    public List<Brand> findAllOrderById() {
        return brandRepository.findAllOrderById();
    }

    public Optional<Brand> findById(int brandId) {
        return brandRepository.findById(brandId);
    }
}
