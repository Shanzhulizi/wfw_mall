package com.lm.cart.task;

import com.lm.cart.dto.CartPersistDTO;
import com.lm.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
/**
 * 这是一个 Spring 组件，负责定时从 Redis 读取所有购物车数据，批量同步到数据库。
 *
 * 通过 @Scheduled 注解，每隔10分钟执行一次。
 */
@Component
public class CartPersistTask {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CartRepository cartItemRepository;

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void syncCartData() {
        Set<String> keys = redisTemplate.keys("cart:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long userId = Long.valueOf(key.split(":")[1]);
            Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(key);

            for (Map.Entry<Object, Object> entry : cartEntries.entrySet()) {
                Long skuId = Long.valueOf((String) entry.getKey());
                Integer quantity = Integer.valueOf((String) entry.getValue());

                // 查数据库是否已有该商品记录
                Optional<CartPersistDTO> optional = cartItemRepository.findByUserIdAndSkuId(userId, skuId);

                if (optional.isPresent()) {
                    CartPersistDTO cartItem = optional.get();
                    cartItem.setQuantity(quantity);
                    cartItem.setUpdatedAt(LocalDateTime.now());
                    cartItemRepository.save(cartItem);
                } else {
                    CartPersistDTO cartItem = new CartPersistDTO();
                    cartItem.setUserId(userId);
                    cartItem.setSkuId(skuId);
                    cartItem.setQuantity(quantity);
                    cartItem.setUpdatedAt(LocalDateTime.now());
                    cartItemRepository.save(cartItem);
                }
            }
        }
    }
}
