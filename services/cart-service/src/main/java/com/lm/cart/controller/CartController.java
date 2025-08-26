package com.lm.cart.controller;


import com.lm.cart.domain.CartItem;
import com.lm.cart.dto.AddCartDTO;
import com.lm.cart.service.CartService;
import com.lm.common.R;
import com.lm.utils.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车接口", description = "购物车相关操作")
@Slf4j
@Controller
@ResponseBody
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 对添加购物车的功能进行扩展，支持非登录用户添加商品到购物车
     */
    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public R addToCart(@RequestBody AddCartDTO addCartDTO,
                       // 如果是非登录用户，则传入userKey
                       @RequestParam(required = false) String userKey
    ) {
        Long userId = null;
        if (UserContextHolder.getUser().getId() != null) {
            userId = UserContextHolder.getUser().getId();
        }

        String cartKey;
        if (userId != null) {
            cartKey = "cart:" + userId;
        } else {
            if (userKey == null || userKey.isEmpty()) {
                return R.error("请提供游客标识 userKey");
            }
            cartKey = "cart:" + userKey;
        }
        try {
            cartService.addToCart(cartKey, addCartDTO.getSkuId(), addCartDTO.getCount());
        } catch (Exception e) {
            return R.error("减少失败");
        }
        return R.ok("添加成功");
    }


    /**
     * 减少购物车商品数量
     */
    @PostMapping("/decrease")
    public R decreaseFromCart(@RequestBody AddCartDTO addCartDTO,
                              // 如果是非登录用户，则传入userKey
                              @RequestParam(required = false) String userKey
    ) {
        Long userId = null;
        if (UserContextHolder.getUser().getId() != null) {
            userId = UserContextHolder.getUser().getId();
        }

        String cartKey;
        if (userId != null) {
            cartKey = "cart:" + userId;
        } else {
            if (userKey == null || userKey.isEmpty()) {
                return R.error("请提供游客标识 userKey");
            }
            cartKey = "cart:" + userKey;
        }

        try {
            cartService.decreaseFromCart(cartKey, addCartDTO.getSkuId(), addCartDTO.getCount());
        } catch (Exception e) {
            return R.error("减少失败");
        }
        return R.ok("减少成功");
    }

    @Operation(summary = "展示购物车商品")
    @GetMapping("/list")
    public R listCart(@RequestParam(required = false) String userKey
    ) {
        Long userId = null;
        if (UserContextHolder.getUser().getId() != null) {
            userId = UserContextHolder.getUser().getId();
        }
        log.info("userId: {}, userKey: {}", userId, userKey);
        List<CartItem> cartItems = cartService.listCart(userId,userKey);
        log.info("cartItems: {}", cartItems);
        return R.ok("展示购物车商品", cartItems);
    }
}
