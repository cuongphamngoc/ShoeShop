package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> getAll();

    List<String> getAllBrandName();

    Brand findById(Long id);
}
