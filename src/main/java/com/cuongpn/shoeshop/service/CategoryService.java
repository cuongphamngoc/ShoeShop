package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    public List<Category> getAll();
    public Category findById(Long id);

    public Set<Category> findAllById(Set<Long> id);

}
