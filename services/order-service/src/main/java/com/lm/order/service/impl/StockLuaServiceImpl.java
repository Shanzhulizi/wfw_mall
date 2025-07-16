package com.lm.order.service.impl;

import com.lm.order.service.StockLuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockLuaServiceImpl implements StockLuaService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 加载并执行扣库存 Lua 脚本
     */
    @Override
    public boolean deductStock(List<String> keys, List<Integer> quantities) {
        try {
            // 1. 加载 Lua 脚本
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(loadLuaScript("script/deduct_stock_batch.lua")); // 文件放在 resources/lua/
            redisScript.setResultType(Long.class);

            // 2. 执行脚本
            List<String> redisKeys = keys;  // 如：product:stock:1, product:stock:2
            List<String> args = new ArrayList<>();
            for (Integer quantity : quantities) {
                args.add(String.valueOf(quantity));
            }

            Long result = stringRedisTemplate.execute(redisScript, redisKeys, args.toArray());
            return result != null && result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 工具：加载 Lua 脚本为字符串
     */
    private String loadLuaScript(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder script = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            script.append(line).append("\n");
        }
        return script.toString();
    }
}
