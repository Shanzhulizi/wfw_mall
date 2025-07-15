-- KEYS: 所有库存键，如 product:stock:1, product:stock:2
-- ARGV: 每个商品对应的购买数量

for i = 1, #KEYS do
    local stock = tonumber(redis.call("GET", KEYS[i]))
    local buy = tonumber(ARGV[i])
    if not stock or stock < buy then
        return 0 -- 库存不足，失败
    end
end

-- 如果都通过库存校验，开始扣库存
for i = 1, #KEYS do
    local buy = tonumber(ARGV[i])
    redis.call("DECRBY", KEYS[i], buy)
end

return 1 -- 成功

