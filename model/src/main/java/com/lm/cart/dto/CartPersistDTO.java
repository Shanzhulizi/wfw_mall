package com.lm.cart.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Data
public class CartPersistDTO {
    @Id
    private Long id;

    private Long userId;

    private Long skuId;

    private Integer quantity;

    private LocalDateTime updatedAt;

}
