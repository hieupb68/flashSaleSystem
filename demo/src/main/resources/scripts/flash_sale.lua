-- KEYS[1] = product:stock:{productId}
-- KEYS[2] = product:purchased:{productId}
-- ARGV[1] = userId
-- ARGV[2] = quantity

-- Check if user has already purchased
local hasPurchased = redis.call('SISMEMBER', KEYS[2], ARGV[1])
if hasPurchased == 1 then
    return -1  -- User has already purchased
end

-- Check and decrease stock
local stock = redis.call('GET', KEYS[1])
if not stock or tonumber(stock) < tonumber(ARGV[2]) then
    return -2  -- Not enough stock
end

-- Decrease stock and add user to purchased set
redis.call('DECRBY', KEYS[1], ARGV[2])
redis.call('SADD', KEYS[2], ARGV[1])

return 1  -- Success 