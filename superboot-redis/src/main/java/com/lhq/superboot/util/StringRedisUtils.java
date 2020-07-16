package com.lhq.superboot.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 存放redis数据，为String类型
 * @author: lihaoqi
 * @date: 2019年11月28日 上午11:24:29
 * @version: v1.0.0
 */
@Component
public class StringRedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
                stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     * @Description: 根据key 获取过期时间
     */
    public long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * @param key 键
     * @return true 存在 false不存在
     * @Description: 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key 可以传一个值 或多个
     * @Description: 删除缓存
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                stringRedisTemplate.delete(key[0]);
            } else {
                stringRedisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    // ============================String=============================

    /**
     * @param key 键
     * @return 值
     * @Description: 普通缓存获取
     */
    public String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     * @Description: 普通缓存放入
     */
    public boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
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
    public boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key      键
     * @param valueObj 值
     * @param time     时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     * @Description: 普通缓存放入并设置时间
     */
    public boolean set(String key, Object valueObj, long time) {
        try {
            String value = StringEscapeUtils.unescapeJavaScript(JSONObject.toJSONString(valueObj));
            if (time > 0) {
                stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key          键
     * @param validityTime 过期时间
     * @return
     * @Description: 递增
     */
    public long incr(String key, long validityTime) {
        return stringRedisTemplate.opsForValue().increment(key, validityTime);
    }

    /**
     * @param key          键
     * @param validityTime 过期时间
     * @return
     * @Description: 递减
     */
    public long decr(String key, long validityTime) {
        return stringRedisTemplate.opsForValue().increment(key, validityTime);
    }

    /**
     * @param key
     * @return
     * @Description: 使用scan命令 查询某些前缀的key
     */
    public Set<String> scan(String key) {
        return this.stringRedisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {

                Set<String> binaryKeys = new HashSet<>();

                Cursor<byte[]> cursor = connection
                        .scan(new ScanOptions.ScanOptionsBuilder().match(key).count(1000).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            }
        });
    }

    /**
     * @param key
     * @return
     * @Description: 使用scan命令 查询某些前缀的key 有多少个 可以用来获取当前session数量,也就是在线用户
     */
    public Long scanSize(String key) {
        return this.stringRedisTemplate.execute(new RedisCallback<Long>() {
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
    }

    /**
     * @param keys
     * @return
     * @Description: 通过keys获取String的值
     */
    public List<String> getValueByKeys(Set<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    // ================================Map=================================

    /**
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     * @Description: HashGet
     */
    public Object hget(String key, String item) {
        return stringRedisTemplate.opsForHash().get(key, item);
    }

    /**
     * @param key 键
     * @return 对应的多个键值
     * @Description: 获取hashKey对应的所有键值
     */
    public Map<Object, Object> hmget(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     * @Description: HashSet
     */
    public boolean hmset(String key, Map<String, String> map) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
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
    public boolean hmset(String key, Map<String, String> map, long time) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
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
    public boolean hset(String key, String item, String value) {
        try {
            stringRedisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
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
    public boolean hset(String key, String item, String value, long time) {
        try {
            stringRedisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     * @Description: 删除hash表中的值
     */
    public void hdel(String key, Object... item) {
        stringRedisTemplate.opsForHash().delete(key, item);
    }

    /**
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     * @Description: 判断hash表中是否有该项的值
     */
    public boolean hHasKey(String key, String item) {
        return stringRedisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     * @Description: hash递增 如果不存在,就会创建一个 并把新增后的值返回
     */
    public double hincr(String key, String item, double by) {
        return stringRedisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     * @Description: hash递减
     */
    public double hdecr(String key, String item, double by) {
        return stringRedisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * @param key 键
     * @return
     * @Description: 根据key获取Set中的所有值
     */
    public Set<String> sGet(String key) {
        try {
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     * @Description: 根据value从一个set中查询, 是否存在
     */
    public boolean sHasKey(String key, String value) {
        try {
            return stringRedisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     * @Description: 将数据放入set缓存
     */
    public long sSet(String key, String... values) {
        try {
            return stringRedisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
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
    public long sSetAndTime(String key, long time, String... values) {
        try {
            Long count = stringRedisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
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
            return stringRedisTemplate.opsForSet().size(key);
        } catch (Exception e) {
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
            return stringRedisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
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
    public List<String> lGet(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
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
            return stringRedisTemplate.opsForList().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     * @Description: 通过索引 获取list中的值
     */
    public String lGetIndex(String key, long index) {
        try {
            return stringRedisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往尾加元素)
     */
    public boolean lrSet(String key, String value) {
        try {
            stringRedisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往头加元素)
     */
    public boolean llSet(String key, String value) {
        try {
            stringRedisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
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
    public boolean lrSet(String key, String value, long time) {
        try {
            stringRedisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
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
    public boolean llSet(String key, String value, long time) {
        try {
            stringRedisTemplate.opsForList().leftPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往尾加元素)
     */
    public boolean lrSetList(String key, List<String> value) {
        try {
            stringRedisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param key   键
     * @param value 值
     * @return
     * @Description: 将list放入缓存(往头加元素)
     */
    public boolean llSetList(String key, List<String> value) {
        try {
            stringRedisTemplate.opsForList().leftPushAll(key, value);
            return true;
        } catch (Exception e) {
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
    public boolean lrSet(String key, List<String> value, long time) {
        try {
            stringRedisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
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
    public boolean llSet(String key, List<String> value, long time) {
        try {
            stringRedisTemplate.opsForList().leftPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
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
    public boolean lUpdateIndex(String key, long index, String value) {
        try {
            stringRedisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
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
    public long lRemove(String key, long count, String value) {
        try {
            return stringRedisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
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
            return stringRedisTemplate.opsForList().size(key);
        } catch (Exception e) {
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
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String key, Set<TypedTuple<String>> values) {
        return stringRedisTemplate.opsForZSet().add(key, values);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * @param key
     * @param value
     * @param delta
     * @return
     * @Description: 增加元素的score值，并返回增加后的值
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * @param key
     * @param value
     * @return 0表示第一位
     * @Description: 返回元素在集合的排名, 有序集合是按照元素的score值由小到大排列
     */
    public Long zRank(String key, String value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     * @Description: 返回元素在集合的排名, 按元素的score值由大到小排列
     */
    public Long zReverseRank(String key, String value) {
        return stringRedisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * @param key
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return
     * @Description: 获取集合的元素, 从小到大排序
     */
    public Set<String> zRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 获取集合元素, 并且把score值也获取
     */
    public Set<TypedTuple<String>> zRangeWithScores(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     * @Description: 根据Score值查询集合元素
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     * @Description: 根据Score值查询集合元素, 从小到大排序
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 获取集合的元素, 从大到小排序
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 获取集合的元素, 从大到小排序, 并返回score值
     */
    public Set<TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据Score值查询集合元素, 从大到小排序
     */
    public Set<String> zReverseRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据Score值查询集合元素, 从大到小排序
     */
    public Set<TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<String> zReverseRangeByScore(String key, double min, double max, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据score值获取集合元素数量
     */
    public Long zCount(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * @param key
     * @return
     * @Description: 获取集合大小
     */
    public Long zSize(String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }

    /**
     * @param key
     * @return
     * @Description: 获取集合大小
     */
    public Long zZCard(String key) {
        return stringRedisTemplate.opsForZSet().zCard(key);
    }

    /**
     * @param key
     * @param value
     * @return
     * @Description: 获取集合中value元素的score值
     */
    public Double zScore(String key, String value) {
        return stringRedisTemplate.opsForZSet().score(key, value);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @Description: 移除指定索引位置的成员
     */
    public Long zRemoveRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @return
     * @Description: 根据指定的score值的范围来移除成员
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     * @Description: 获取key和otherKey的并集并存储在destKey中
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     * @Description: 交集
     */
    public Long zIntersectAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     * @Description: 交集
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * @param key
     * @param options
     * @return
     */
    public Cursor<TypedTuple<String>> zScan(String key, ScanOptions options) {
        return stringRedisTemplate.opsForZSet().scan(key, options);
    }

}