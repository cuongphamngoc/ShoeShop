package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Size,Long> {
    public Size findByValue(Long value);
}
