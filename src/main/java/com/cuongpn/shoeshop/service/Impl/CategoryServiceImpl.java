package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Category;
import com.cuongpn.shoeshop.repository.CategoryRepository;
import com.cuongpn.shoeshop.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    @Override
    public Set<Category> findAllById(Set<Long> id) {
        return new HashSet<>(categoryRepository.findAllById(id));
    }
}
