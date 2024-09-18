package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.AddCartRequest;
import com.cuongpn.shoeshop.dto.UpdateQuantityRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.ProductSize;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.repository.CartRepository;
import com.cuongpn.shoeshop.service.CartService;
import com.cuongpn.shoeshop.service.ProductSizeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductSizeService productSizeService;
    @Override
    public Cart findCartByUser(User user) {
        Optional<Cart>  cart = cartRepository.findByUser(user);
        if(cart.isPresent()) return cart.get();
        Cart newCart = new Cart();
        newCart.setUser(user);
        cartRepository.save(newCart);
        return newCart;
    }

    @Override
    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    @Override
    public int getItemsNumber(User user) {
        return this.findCartByUser(user).getCartItems().size();

    }

    @Override
    public void deleteCartItem(List<Long> cartIds, Cart cart) {
        List<CartItem> cartItems = cart.getCartItems();
        for(CartItem cartItem:cartItems){
            if(cartIds.contains(cartItem.getId())){
                ProductSize productSize = cartItem.getProductSize();
                productSize.setStock(productSize.getStock() + cartItem.getQty());
                productSizeService.saveProductSize(productSize);
            }
        }
        cartItems.removeIf(cartItem -> cartIds.contains(cartItem.getId()));
        cartRepository.save(cart);
    }

    @Override
    public void updateCartItem(Cart cart, UpdateQuantityRequest updateQuantityRequest) {
        List<CartItem> cartItems = cart.getCartItems();
        Optional<CartItem>optionalCartItem = cartItems.stream().filter(cartItem1 -> Objects.equals(cartItem1.getId(), updateQuantityRequest.getCartItemId())).findFirst();
        if(optionalCartItem.isPresent()){
            CartItem cartItem = optionalCartItem.get();
            cartItems.remove(cartItem);
            ProductSize productSize = cartItem.getProductSize();
            int nextStock = productSize.getStock() -  updateQuantityRequest.getStock();

            if(nextStock < 0) throw new RuntimeException("Out of stock!");
            productSize.setStock(nextStock);
            cartItem.setQty(cartItem.getQty()+ updateQuantityRequest.getStock());
            cartItems.add(cartItem);
            cartRepository.save(cart);
        }
        else {
            throw  new RuntimeException("CartItem not found");
        }

    }


}
