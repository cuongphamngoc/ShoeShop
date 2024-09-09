package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Brand;
import com.cuongpn.shoeshop.repository.BrandRepository;
import com.cuongpn.shoeshop.service.BrandService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id).orElseThrow();
    }
}
