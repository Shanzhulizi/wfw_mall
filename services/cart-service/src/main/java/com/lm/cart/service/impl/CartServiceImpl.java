package com.lm.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.lm.cart.domain.CartItem;
import com.lm.cart.feign.ProductFeignClient;
import com.lm.cart.mapper.CartMapper;
import com.lm.cart.service.CartService;
import com.lm.product.dto.ProductCartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public void addToCart(String cartKey, Long skuId, Integer quantity) {

        // 1. 从 Redis 取出已有商品
        String itemJson = (String) stringRedisTemplate.opsForHash().get(cartKey, skuId.toString());
        //处了skuId和quantity之外，还有其他属性来组成购物车，这些属性从数据库查
        CartItem cartItem;
        if (StringUtils.hasText(itemJson)) {
            //如果redis中已经存在该商品，则更新数量
            cartItem = JSON.parseObject(itemJson, CartItem.class);
            cartItem.setCount(cartItem.getCount() + quantity);
        } else {
            // 不存在，调用商品服务获取商品信息

            ProductCartDTO product =
                    productFeignClient.getProductById(skuId);

            if (product == null) {
                throw new RuntimeException("商品不存在：" + skuId);
            }
            cartItem = new CartItem();
            cartItem.setSkuId(skuId);
            cartItem.setSkuName(product.getSkuName());
            cartItem.setImage(product.getImage());
            cartItem.setPrice(product.getPrice());
            cartItem.setCount(quantity);
            cartItem.setChecked(false); //默认不选中

        }
        // 2. 更新 Redis 中的购物车
        stringRedisTemplate.opsForHash().put(cartKey, skuId.toString(), JSON.toJSONString(cartItem));

        // 3. 设置购物车过期时间（30天）
        stringRedisTemplate.expire(cartKey, Duration.ofDays(30));
    }

    @Override
    public void decreaseFromCart(String cartKey, Long skuId, Integer count) {

        // 1. 从 Redis 查出商品
        String itemJson = (String) stringRedisTemplate.opsForHash().get(cartKey, skuId.toString());

        if (itemJson == null) {
            throw new RuntimeException("购物车中没有该商品");
        }
        CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
        cartItem.setCount(cartItem.getCount() - count);


        if (cartItem.getCount() <= 0) {
            // 如果数量小于等于0，直接从购物车移除
            stringRedisTemplate.opsForHash().delete(cartKey, skuId.toString());
        } else {
            // 更新 Redis
            stringRedisTemplate.opsForHash().put(cartKey, skuId.toString(), JSON.toJSONString(cartItem));
        }


    }

    @Override
    public List<CartItem> listCart(Long userId, String userKey) {
        List<Object> values;
        if (userId == null) {
            if (!StringUtils.hasText(userKey)) {
                return Collections.emptyList();
            }
            values = stringRedisTemplate.opsForHash().values("cart:" + userKey);

        } else {
            values = stringRedisTemplate.opsForHash().values("cart:" + userId);

        }
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        List<CartItem> cartItems = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof String) {
                CartItem item = JSON.parseObject((String) value, CartItem.class);
                cartItems.add(item);
            }
        }
        return cartItems;
    }

}
