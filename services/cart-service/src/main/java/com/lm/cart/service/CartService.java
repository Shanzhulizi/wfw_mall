package com.lm.cart.service;

import com.lm.cart.domain.CartItem;

import java.util.List;

public interface CartService {
    void addToCart(String cartKey, Long skuId, Integer quantity);

    void decreaseFromCart(String cartKey, Long skuId, Integer count);

    List<CartItem>  listCart(Long userId, String userKey);

//    List<CartItem> listCart(Long userId);
}
