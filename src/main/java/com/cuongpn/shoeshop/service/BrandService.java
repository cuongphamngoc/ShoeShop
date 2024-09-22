package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> getAll();

    Brand findById(Long id);
}
