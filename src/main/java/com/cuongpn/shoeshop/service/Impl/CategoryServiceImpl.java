package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Category;
import com.cuongpn.shoeshop.repository.CategoryRepository;
import com.cuongpn.shoeshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Cacheable("categories")
    public List<String> getAllCategoryName() {
        return getAll().stream().map(Category::getName).collect(Collectors.toList());
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
