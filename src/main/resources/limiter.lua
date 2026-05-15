local key = KEYS[1]
local window = tonumber(ARGV[1]) -- 时间窗口（单位：秒）
local limit = tonumber(ARGV[2]) -- 时间窗口内允许的请求数
local now = tonumber(ARGV[3]) -- 当前时间（单位：毫秒）

-- 参数为空时直接返回错误
if not window or not limit or not now then
    return redis.error_reply("Invalid input parameters")
end

window = window * 1000

-- 删除窗口外的请求记录
redis.call('ZREMRANGEBYSCORE', key, 0, now - window)

-- 统计当前窗口内的请求数
local current = redis.call('ZCARD', key)

if current < limit then
    -- 使用时间戳 + 随机数作为 member，避免同毫秒请求冲突
    math.randomseed(now)
    local random = math.random(1000000)
    redis.call('ZADD', key, now, now .. '-' .. random)
    redis.call('EXPIRE', key, math.ceil(window / 1000))
    return current + 1
else
    return 0
end
