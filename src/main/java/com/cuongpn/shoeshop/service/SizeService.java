package com.cuongpn.shoeshop.service;


import com.cuongpn.shoeshop.entity.Size;

import java.util.List;
import java.util.Set;

public interface SizeService {
    public List<Size> getAll();

    public Size findById(Long id);

    public Set<Size> findAllById(Set<Long> id);

    public void saveSize(Size size);

    public Size findByValue(Long size);
}
