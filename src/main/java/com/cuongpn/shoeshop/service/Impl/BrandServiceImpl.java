package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Brand;
import com.cuongpn.shoeshop.repository.BrandRepository;
import com.cuongpn.shoeshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }
    @Override
    @Cacheable("brands")
    public List<String> getAllBrandName(){
        return getAll().stream().map(Brand::getName).collect(Collectors.toList());
    }

    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id).orElseThrow();
    }
}
