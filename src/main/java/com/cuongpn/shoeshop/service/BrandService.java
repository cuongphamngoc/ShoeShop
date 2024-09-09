package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Brand;

import java.util.List;

public interface BrandService {
    public List<Brand> getAll();

    public Brand findById(Long id);
}
