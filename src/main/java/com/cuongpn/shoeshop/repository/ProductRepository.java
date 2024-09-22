package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(value = "Product.detail", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull
    List<Product> findAll();
    @EntityGraph(value = "Product.detail", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull
    Optional<Product> findById(@NonNull Long id);
    @Query("SELECT p FROM Product p JOIN p.productSizes ps JOIN ps.orderItems oi " +
            "GROUP BY p.id ORDER BY SUM(oi.qty) DESC")
    Page<Product> findProductFeatured(Pageable pageable);
}
