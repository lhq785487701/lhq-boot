package com.lhq.superboot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: redis工具类，使用自己的序列化方式（config中）
 * @author: lihaoqi
 * @date: 2019年4月15日
 * @version: 1.0.0
 */
@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // =============================common============================

    /**
     * @param key  键
     * @param time 时间(秒)
     * @return
     * @Description: 指定缓存失效时间
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param oldKey
     * @param newKey
     * @Description:修改 key 的名称
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * @param oldKey
     * @param newKey
     * @return
     * @Description:仅当 newkey 不存在时，将 oldKey 改名为 newkey
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     * @Description: 根据key 获取过期时间
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * @param key 键
     * @return true 存在 false不存在
     * @Description: 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key
     * @return
     * @Description: 返回 key 所储存的值的类型
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    /**
     * @param key 可以传一个值 或多个
     * @Description: 删除缓存
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String=============================

    /**
     * @param key 键
     * @return 值
     * @Description: 普通缓存获取
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     * @Description: 普通缓存放入
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }

    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     * @Description: 普通缓存放入并设置时间
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     * @Description: 递增
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        long data = (long) redisTemplate.opsForValue().get(key);
        long result = data + delta;
        redisTemplate.opsForValue().set(key, result);
        return result;
        // return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     * @Description: 递减
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        long data = (long) redisTemplate.opsForValue().get(key);
        long result = data - delta;
        redisTemplate.opsForValue().set(key, result);
        return result;
        // return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * @param key
     * @return
     * @Description: 使用scan命令 查询某些前缀的key
     */
    public Set<String> scan(String key) {
        Set<String> execute = this.redisTemplate.execute(new RedisCallback<Set<String>>() {

            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {

                Set<String> binaryKeys = new HashSet<>();

                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(key).count(1000).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            }
        });
        return execute;
    }

    /**
     * @param key
     * @return
     * @Description: 使用scan命令 查询某些前缀的key 有多少个 可以用来获取当前session数量,也就是在线用户
     */
    public Long scanSize(String key) {
        long dbSize = this.redisTemplate.execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long count = 0L;
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(key).count(1000).build());
                while (cursor.hasNext()) {
                    cursor.next();
                    count++;
                }
                return count;
            }
        });
        return dbSize;
    }

    /**
     * @param keys
     * @return
     * @Description: 通过keys获取String的值
     */
    public List<Object> getValueByKeys(Set<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    // ================================Map=================================

    /**
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     * @Description: HashGet
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * @param key 键
     * @return 对应的多个键值
     * @Description: 获取hashKey对应的所有键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     * @Description: HashSet
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     * @Description: HashSet 并设置时间
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     * @Description: 向一张hash表中放入数据, 如果不存在将创建
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     * @Description: 向一张hash表中放入数据, 如果不存在将创建
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     * @Description: 删除hash表中的值
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     * @Description: 判断hash表中是否有该项的值
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     * @Description: hash递增 如果不存在,就会创建一个 并把新增后的值返回
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     * @Description: hash递减
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * @param key 键
     * @return
     * @Description: 根据key获取Set中的所有值
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return null;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     * @Description: 根据value从一个set中查询, 是否存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     * @Description: 将数据放入set缓存
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return 0;
        }
    }

    /**
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     * @Description: 将set数据放入缓存
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return 0;
        }
    }

    /**
     * @param key 键
     * @return
     * @Description: 获取set缓存的长度
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return 0;
        }
    }

    /**
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     * @Description: 移除值为value的
     */
    public long setRemove(String key, Object... values) {
        try {
            long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     * @Description: 获取list缓存的内容
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return null;
        }
    }

    /**
     * @param key 键
     * @return
     * @Description: 获取list缓存的长度
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return 0;
        }
    }

    /**
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     * @Description: 通过索引 获取list中的值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return null;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往尾加元素)
     */
    public boolean lrSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往头加元素)
     */
    public boolean llSet(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     * @Description: 将list放入缓存，加过期时间(往尾加元素)
     */
    public boolean lrSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     * @Description: 将list放入缓存，加过期时间(往头加元素)
     */
    public boolean llSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往尾加元素)
     */
    public boolean lrSetList(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往头加元素)
     */
    public boolean llSetList(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     * @Description: 将list放入缓存(往尾加元素)
     */
    public boolean lrSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     * @Description: 将list放入缓存(往头加元素)
     */
    public boolean llSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     * @Description: 根据索引修改list中的某条数据
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     * @Description: 移除N个值为value 从存储在键中的列表中删除等于值的元素的第一个计数事件。count>
     * 0：删除等于从左到右移动的值的第一个元素；count< 0：删除等于从右到左移动的值的第一个元素；count =
     * 0：删除等于value的所有元素
     */
    public long lRemove(String key, long count, Object value) {
        try {
            long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return 0;
        }
    }

    /**
     * @param key
     * @return
     * @Description: 获取列表长度
     */
    public long llen(String key) {
        try {
            long size = redisTemplate.opsForList().size(key);
            return size;
        } catch (Exception e) {
            logger.error("[RedisUtils] -> redis异常：{}", e.getMessage());
            return 0;
        }
    }

    // ===============================zSet=================================

    /**
     * @param key
     * @param value
     * @param score
     * @return
     * @Description: 添加元素, 有序集合是按照元素的score值由小到大排列
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String key, Set<TypedTuple<Object>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * @param key
     * @param value
     * @param delta
     * @return
     * @Description: 增加元素的score值，并返回增加后的值
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * @param key
     * @param value
     * @return 0表示第一位
     * @Description: 返回元素在集合的排名, 有序集合是按照元素的score值由小到大排列
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     * @Description: 返回元素在集合的排名, 按元素的score值由大到小排列
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * @param key
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return
     * @Description: 获取集合的元素, 从小到大排序
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 获取集合元素, 并且把score值也获取
     */
    public Set<TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     * @Description: 根据Score值查询集合元素
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     * @Description: 根据Score值查询集合元素, 从小到大排序
     */
    public Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 获取集合的元素, 从大到小排序
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 获取集合的元素, 从大到小排序, 并返回score值
     */
    public Set<TypedTuple<Object>> zReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据Score值查询集合元素, 从大到小排序
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据Score值查询集合元素, 从大到小排序
     */
    public Set<TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据score值获取集合元素数量
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * @param key
     * @return
     * @Description: 获取集合大小
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * @param key
     * @return
     * @Description: 获取集合大小
     */
    public Long zZCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * @param key
     * @param value
     * @return
     * @Description: 获取集合中value元素的score值
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 移除指定索引位置的成员
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据指定的score值的范围来移除成员
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     * @Description: 获取key和otherKey的并集并存储在destKey中
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     * @Description: 交集
     */
    public Long zIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     * @Description: 交集
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * @param key
     * @param options
     * @return
     */
    public Cursor<TypedTuple<Object>> zScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }

}
