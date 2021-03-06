package com.lhq.superboot.util;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * @Description: mongodb curd 工具类
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version:1.0.0
 */
@Component
public class MongodbUtils {

    private static MongodbUtils mongodbUtils;

    @PostConstruct
    public void init() {
        mongodbUtils = this;
        mongodbUtils.mongoTemplate = this.mongoTemplate;
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param obj 数据对象
     * @Description: 保存数据对象，集合为数据对象中@Document 注解所配置的collection
     */
    public static void save(Object obj) {

        mongodbUtils.mongoTemplate.save(obj);
    }

    /**
     * @param obj            数据对象
     * @param collectionName 集合名
     * @Description: 指定集合保存数据对象
     */
    public static void save(Object obj, String collectionName) {

        mongodbUtils.mongoTemplate.save(obj, collectionName);
    }

    /**
     * @param obj 数据对象
     * @Description: 根据数据对象中的id删除数据，集合为数据对象中@Document 注解所配置的collection
     */
    public static void remove(Object obj) {

        mongodbUtils.mongoTemplate.remove(obj);
    }

    /**
     * @param obj            数据对象
     * @param collectionName 集合名
     * @Description: 指定集合 根据数据对象中的id删除数据
     */
    public static void remove(Object obj, String collectionName) {

        mongodbUtils.mongoTemplate.remove(obj, collectionName);
    }

    /**
     * @param key            键
     * @param value          值
     * @param collectionName 集合名
     * @Description: 根据key，value到指定集合删除数据
     */
    public static void removeById(String key, Object value, String collectionName) {

        Criteria criteria = Criteria.where(key).is(value);
        criteria.and(key).is(value);
        Query query = Query.query(criteria);
        mongodbUtils.mongoTemplate.remove(query, collectionName);
    }

    /**
     * @param accordingKey   修改条件 key
     * @param accordingValue 修改条件 value
     * @param updateKeys     修改内容 key数组
     * @param updateValues   修改内容 value数组
     * @param collectionName 集合名
     * @Description: 指定集合 修改数据，且仅修改找到的第一条数据
     */
    public static void updateFirst(String accordingKey, Object accordingValue, String[] updateKeys,
                                   Object[] updateValues, String collectionName) {

        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);
        Update update = new Update();
        for (int i = 0; i < updateKeys.length; i++) {
            update.set(updateKeys[i], updateValues[i]);
        }
        mongodbUtils.mongoTemplate.updateFirst(query, update, collectionName);
    }

    /**
     * @param accordingKey   修改条件 key
     * @param accordingValue 修改条件 value
     * @param updateKeys     修改内容 key数组
     * @param updateValues   修改内容 value数组
     * @param collectionName 集合名
     * @Description: 指定集合 修改数据，且修改所找到的所有数据
     */
    public static void updateMulti(String accordingKey, Object accordingValue, String[] updateKeys,
                                   Object[] updateValues, String collectionName) {

        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);
        Update update = new Update();
        for (int i = 0; i < updateKeys.length; i++) {
            update.set(updateKeys[i], updateValues[i]);
        }
        mongodbUtils.mongoTemplate.updateMulti(query, update, collectionName);
    }

    /**
     * @param obj        数据对象
     * @param findKeys   查询条件 key
     * @param findValues 查询条件 value
     * @return
     * @Description: 根据条件查询出所有结果集 集合为数据对象中@Document 注解所配置的collection
     */
    public static List<? extends Object> find(Object obj, String[] findKeys, Object[] findValues) {

        Criteria criteria = null;
        for (int i = 0; i < findKeys.length; i++) {
            if (i == 0) {
                criteria = Criteria.where(findKeys[i]).is(findValues[i]);
            } else {
                criteria.and(findKeys[i]).is(findValues[i]);
            }
        }
        Query query = Query.query(criteria);
        List<? extends Object> resultList = mongodbUtils.mongoTemplate.find(query, obj.getClass());
        return resultList;
    }

    /**
     * @param obj            数据对象
     * @param findKeys       查询条件 key
     * @param findValues     查询条件 value
     * @param collectionName 集合名
     * @return
     * @Description: 指定集合 根据条件查询出所有结果集
     */
    public static List<? extends Object> find(Object obj, String[] findKeys, Object[] findValues,
                                              String collectionName) {

        Criteria criteria = null;
        for (int i = 0; i < findKeys.length; i++) {
            if (i == 0) {
                criteria = Criteria.where(findKeys[i]).is(findValues[i]);
            } else {
                criteria.and(findKeys[i]).is(findValues[i]);
            }
        }
        Query query = Query.query(criteria);
        List<? extends Object> resultList = mongodbUtils.mongoTemplate.find(query, obj.getClass(), collectionName);
        return resultList;
    }

    /**
     * @param obj            数据对象
     * @param findKeys       查询条件 key
     * @param findValues     查询条件 value
     * @param collectionName 集合名
     * @param sort           排序字段
     * @return
     * @Description: 指定集合 根据条件查询出所有结果集 并排倒序
     */
    public static List<? extends Object> find(Object obj, String[] findKeys, Object[] findValues, String collectionName,
                                              String sort) {

        Criteria criteria = null;
        for (int i = 0; i < findKeys.length; i++) {
            if (i == 0) {
                criteria = Criteria.where(findKeys[i]).is(findValues[i]);
            } else {
                criteria.and(findKeys[i]).is(findValues[i]);
            }
        }
        Query query = Query.query(criteria);
        query.with(new Sort(Direction.DESC, sort));
        List<? extends Object> resultList = mongodbUtils.mongoTemplate.find(query, obj.getClass(), collectionName);
        return resultList;
    }

    /**
     * @param obj        数据对象
     * @param findKeys   查询条件 key
     * @param findValues 查询条件 value
     * @return
     * @Description: 根据条件查询出符合的第一条数据 集合为数据对象中 @Document 注解所配置的collection
     */
    public static Object findOne(Object obj, String[] findKeys, Object[] findValues) {

        Criteria criteria = null;
        for (int i = 0; i < findKeys.length; i++) {
            if (i == 0) {
                criteria = Criteria.where(findKeys[i]).is(findValues[i]);
            } else {
                criteria.and(findKeys[i]).is(findValues[i]);
            }
        }
        Query query = Query.query(criteria);
        Object resultObj = mongodbUtils.mongoTemplate.findOne(query, obj.getClass());
        return resultObj;
    }

    /**
     * @param obj            数据对象
     * @param findKeys       查询条件 key
     * @param findValues     查询条件 value
     * @param collectionName 集合名
     * @return
     * @Description: 指定集合 根据条件查询出符合的第一条数据
     */
    public static Object findOne(Object obj, String[] findKeys, Object[] findValues, String collectionName) {

        Criteria criteria = null;
        for (int i = 0; i < findKeys.length; i++) {
            if (i == 0) {
                criteria = Criteria.where(findKeys[i]).is(findValues[i]);
            } else {
                criteria.and(findKeys[i]).is(findValues[i]);
            }
        }
        Query query = Query.query(criteria);
        Object resultObj = mongodbUtils.mongoTemplate.findOne(query, obj.getClass(), collectionName);
        return resultObj;
    }

    /**
     * @param obj 数据对象
     * @return
     * @Description: 查询出所有结果集 集合为数据对象中 @Document 注解所配置的collection
     */
    public static List<? extends Object> findAll(Object obj) {

        List<? extends Object> resultList = mongodbUtils.mongoTemplate.findAll(obj.getClass());
        return resultList;
    }

    /**
     * @param obj            数据对象
     * @param collectionName 集合名
     * @return
     * @Description: 指定集合 查询出所有结果集
     */
    public static List<? extends Object> findAll(Object obj, String collectionName) {

        List<? extends Object> resultList = mongodbUtils.mongoTemplate.findAll(obj.getClass(), collectionName);
        return resultList;

    }
}
