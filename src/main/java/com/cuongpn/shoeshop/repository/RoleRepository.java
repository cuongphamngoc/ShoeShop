package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByName(String roleName);
}
