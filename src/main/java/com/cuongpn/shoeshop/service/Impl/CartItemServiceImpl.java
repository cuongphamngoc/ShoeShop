package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.AddCartRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.ProductSize;
import com.cuongpn.shoeshop.repository.CartItemRepository;
import com.cuongpn.shoeshop.service.CartItemService;
import com.cuongpn.shoeshop.service.ProductSizeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final ProductSizeService productSizeService;
    private final CartItemRepository cartItemRepository;
    @Override
    public ResponseEntity<?> addItemToCart(AddCartRequest addCartRequest, Cart cart) {
        Optional<ProductSize> optionalProductSize = productSizeService.findById(addCartRequest.getProduct_id(),addCartRequest.getSize_id());
        if(optionalProductSize.isEmpty()) throw new RuntimeException("Product not found");
        ProductSize productSize = optionalProductSize.get();
        CartItem cartItem = null;
        Optional<CartItem> optionalCartItem = cartItemRepository.findByProductSize(productSize);
        cartItem = optionalCartItem.orElseGet(() -> CartItem.builder()
                .qty(0)
                .cart(cart)
                .productSize(productSize)
                .price(BigDecimal.valueOf(productSize.getProduct().getPrice()))
                .build());


        cartItem.setQty(cartItem.getQty()+ addCartRequest.getQty());
        cartItem.setTotalPrice();
        cartItemRepository.save(cartItem);
        productSize.setStock(productSize.getStock()- addCartRequest.getQty());
        productSizeService.saveProductSize(productSize);
        return ResponseEntity.ok("Success");
    }

    @Override
    public Optional<CartItem> findByProductSize(ProductSize productSize) {
        return cartItemRepository.findByProductSize(productSize);
    }


}
