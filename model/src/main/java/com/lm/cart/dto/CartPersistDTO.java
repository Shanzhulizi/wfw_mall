package com.lm.cart.dto;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "cart_items") // 根据你的表名调整
@Data
public class CartPersistDTO {

    @Id
    private Long id;

    private Long userId;

    private Long skuId;

    private Integer quantity;

    private LocalDateTime updatedAt;

}
