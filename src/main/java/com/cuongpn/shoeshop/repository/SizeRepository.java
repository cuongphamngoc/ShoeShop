package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Size;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size,Long> {
    Size findByValue(Long value);
    @EntityGraph(value = "size.productSizes",type = EntityGraph.EntityGraphType.FETCH)
    @NonNull
    Optional<Size> findById(@NonNull Long id);


}
