package com.lm.cart.repository;

import com.lm.cart.dto.CartPersistDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 这是一个 JPA 数据库操作接口，继承了 JpaRepository，用于操作购物车持久化实体 CartPersistDTO
 */
@Repository
public interface CartRepository
        extends JpaRepository<CartPersistDTO, Long>
{

    Optional<CartPersistDTO> findByUserIdAndSkuId(Long userId, Long skuId);
}

