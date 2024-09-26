package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.CartDTO;
import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.dto.UpdateQuantityRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.ProductSize;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.mapper.CartItemMapper;
import com.cuongpn.shoeshop.repository.CartRepository;
import com.cuongpn.shoeshop.service.CartService;
import com.cuongpn.shoeshop.service.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductSizeService productSizeService;
    private final CartItemMapper cartItemMapper;
    private final CacheManager cacheManager;
    @Override
    public Cart findCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createAndSaveNewCart(user));
    }
    private Cart createAndSaveNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        cartRepository.save(newCart);
        return newCart;
    }

    @Override
    @Transactional
    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    @Override
    @Cacheable(value = "itemCount", key = "#user.id")
    public int getItemsNumber(User user) {
        return this.findCartByUser(user).getCartItems().size();

    }

    @Override
    @Transactional
    @CachePut(value = "itemCount", key = "#user.id")
    @CacheEvict(value = "cartItem", key ="#user.id")
    public int deleteCartItem(List<Long> cartIds, User user) {
        Cart cart = findCartByUser(user);
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.stream()
                .filter(cartItem -> cartIds.contains(cartItem.getId()))
                .forEach(cartItem -> {
                    ProductSize productSize = cartItem.getProductSize();
                    deleteCacheProduct(productSize);
                    productSize.setStock(productSize.getStock() + cartItem.getQty());
                    productSizeService.saveProductSize(productSize);
                });
        cartItems.removeIf(cartItem -> cartIds.contains(cartItem.getId()));
        cartRepository.save(cart);

        return cart.getCartItems().size();
    }

    @Override
    @Transactional
    @CacheEvict(value = "cartItem", key ="#user.id")
    public ResponseEntity<?> updateCartItem(User user, UpdateQuantityRequest updateQuantityRequest) {
        Cart cart = findCartByUser(user);
        List<CartItem> cartItems = cart.getCartItems();
        CartItem cartItem = cartItems.stream()
                .filter(item -> Objects.equals(item.getId(),updateQuantityRequest.getCartItemId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found :" + updateQuantityRequest.getCartItemId()));
        ProductSize productSize = cartItem.getProductSize();
        updateProductStock(productSize, updateQuantityRequest.getStock());
        updateCartItemQuantity(cartItem, updateQuantityRequest.getStock());
        cartRepository.save(cart);
        deleteCacheProduct(productSize);
        return ResponseEntity.ok(cart.getCartItems().size());
    }

    @Override
    @CachePut(value = "cartItem", key ="#user.id")
    public CartDTO getCartDTOByUser(User user) {
        Cart cart = findCartByUser(user);
        List<CartItemDTO> cartItemDTOList = cartItemMapper.toCartItemDTOs(cart.getCartItems());
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartItems(cartItemDTOList);
        return cartDTO;
    }

    private void updateProductStock(ProductSize productSize, int qty) {
        int nextStock = productSize.getStock() - qty;
        if (nextStock < 0) {
            throw new RuntimeException("Out of stock!");
        }
        productSize.setStock(nextStock);
        productSizeService.saveProductSize(productSize);
    }

    private void updateCartItemQuantity(CartItem cartItem, int qty) {
        cartItem.setQty(cartItem.getQty() + qty);
        cartItem.setTotalPrice();
    }
    private void deleteCacheProduct(ProductSize productSize){
        Objects.requireNonNull(cacheManager.getCache("product")).evict(productSize.getProduct().getId());
    }


}
