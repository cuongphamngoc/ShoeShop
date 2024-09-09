package com.cuongpn.shoeshop.domain;

import com.cuongpn.shoeshop.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductSpecification {

    public static Specification<Product> nameLike(String searchKey) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + searchKey + "%");
    }

    public static Specification<Product> categoryEquals(List<String> category) {
        return (root, query, criteriaBuilder) ->{
            Join<Product, Category> categoryJoin = root.join("categories");
            return categoryJoin.get("name").in(category);

        };

    }

    public static Specification<Product> brandIn(List<String> brands) {
        return (root, query, criteriaBuilder) ->{
            Join<Product, Brand> brandJoin = root.join("brand");
            return brandJoin.get("name").in(brands);
        };

    }

    public static Specification<Product> sizeIn(List<String> sizes) {
        return (root, query, criteriaBuilder) ->{
            Join<Product, ProductSize> productSizeJoin = root.join("productSizes");
            Join<ProductSize, Size> sizeJoin = productSizeJoin.join("size");
            return sizeJoin.get("value").in(sizes);
        };

    }

    public static Specification<Product> priceGreaterThanOrEqual(Long lowPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), lowPrice);
    }

    public static Specification<Product> priceLessThanOrEqual(Long highPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), highPrice);
    }
    public static Specification<Product> buildFilter(List<String> sizes, List<String> categories, List<String> brands,
                                                     String search, Long priceLow, Long priceHigh) {
        return Specification.where(Optional.ofNullable(sizes).filter(list -> !list.isEmpty()).map(ProductSpecification::sizeIn).orElse(null))
                .and(Optional.ofNullable(categories).filter(list -> !list.isEmpty()).map(ProductSpecification::categoryEquals).orElse(null))
                .and(Optional.ofNullable(brands).filter(list -> !list.isEmpty()).map(ProductSpecification::brandIn).orElse(null))
                .and(Optional.ofNullable(search).filter(s -> !s.isEmpty()).map(ProductSpecification::nameLike).orElse(null))
                .and(Optional.ofNullable(priceLow).filter(p -> p >= 0).map(ProductSpecification::priceGreaterThanOrEqual).orElse(null))
                .and(Optional.ofNullable(priceHigh).filter(p -> p >= 0).map(ProductSpecification::priceLessThanOrEqual).orElse(null));
    }


}

