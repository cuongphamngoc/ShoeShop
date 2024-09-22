package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    List<Category> getAll();
    Category findById(Long id);

    Set<Category> findAllById(Set<Long> id);

}
