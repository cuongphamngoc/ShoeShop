package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.AddCartRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.ProductSize;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.repository.CartItemRepository;
import com.cuongpn.shoeshop.service.CartItemService;
import com.cuongpn.shoeshop.service.CartService;
import com.cuongpn.shoeshop.service.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final ProductSizeService productSizeService;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    @Override
    @Transactional
    @CacheEvict(value = "itemCount", key = "#user.id")
    public ResponseEntity<?> addItemToCart(AddCartRequest addCartRequest, User user) {
        Cart cart = cartService.findCartByUser(user);
        ProductSize productSize = productSizeService.findById(addCartRequest.getProduct_id(),
                addCartRequest.getSize_id())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        boolean isStockLessThanRequest = productSize.getStock() < addCartRequest.getQty();
        if (isStockLessThanRequest) {
            throw new RuntimeException("Insufficient stock");
        }
        CartItem cartItem = cartItemRepository.findByProductSize(productSize)
                .orElseGet(()-> createNewCartItem(cart,productSize));

        cartItem.setQty(cartItem.getQty()+ addCartRequest.getQty());
        cartItem.setTotalPrice();
        cartItemRepository.save(cartItem);
        if(!cart.getCartItems().contains(cartItem)){
            cart.getCartItems().add(cartItem);
            cartService.saveCart(cart);
        }

        updateProductStock(productSize,addCartRequest.getQty());

        return ResponseEntity.ok(cart.getCartItems().size());
    }
    private CartItem createNewCartItem(Cart cart, ProductSize productSize) {
        return CartItem.builder()
                .qty(0)
                .cart(cart)
                .productSize(productSize)
                .price(BigDecimal.valueOf(productSize.getProduct().getPrice()))
                .build();
    }
    private void updateProductStock(ProductSize productSize, int qty) {
        productSize.setStock(productSize.getStock() - qty);
        productSizeService.saveProductSize(productSize);
    }


}
