package com.lm;

import com.lm.order.utils.OrderNoGenerator;
import org.junit.jupiter.api.Test;


public class test {

    @Test
    public void test() {
        // 测试代码逻辑

        String s = OrderNoGenerator.generateOrderNo(123456L, 7890L);
        System.out.println(s);

    }

}
