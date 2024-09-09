package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Size;
import com.cuongpn.shoeshop.repository.SizeRepository;
import com.cuongpn.shoeshop.service.SizeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SizeServiceImpl implements SizeService {
    private final SizeRepository sizeRepository;
    @Override
    public List<Size> getAll() {
        return sizeRepository.findAll();
    }

    @Override
    public Size findById(Long id) {
        return sizeRepository.findById(id).orElseThrow();
    }

    @Override
    public Set<Size> findAllById(Set<Long> id) {
        return new HashSet<>(sizeRepository.findAllById(id));
    }

    @Override
    public void saveSize(Size size) {
        sizeRepository.save(size);
    }

    @Override
    public Size findByValue(Long size) {
        return sizeRepository.findByValue(size);
    }
}
