package com.cuongpn.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "Product.detail",
        attributeNodes = {
                @NamedAttributeNode("brand"),
                @NamedAttributeNode("categories"),
                @NamedAttributeNode(value = "productSizes", subgraph = "productSizes"),
                @NamedAttributeNode("productImages")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "productSizes",
                        attributeNodes = @NamedAttributeNode("size")
                )
        }
)
public class Product implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long price;
    @OneToMany(mappedBy="product", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<ProductSize> productSizes = new HashSet<>();
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<ProductImage> productImages = new HashSet<>();
    private String thumbnailImageUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private Brand brand;

    public Product(String title, Long price) {
        this.title = title;
        this.price = price;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
