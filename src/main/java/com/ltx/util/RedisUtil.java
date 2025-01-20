package com.ltx.util;

import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author tianxing
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> stringOperation;
    private HashOperations<String, String, String> hashOperation;
    private ListOperations<String, String> listOperation;
    private SetOperations<String, String> setOperation;
    private ZSetOperations<String, String> zSetOperation;

    @PostConstruct
    public void init() {
        stringOperation = stringRedisTemplate.opsForValue();
        hashOperation = stringRedisTemplate.opsForHash();
        listOperation = stringRedisTemplate.opsForList();
        setOperation = stringRedisTemplate.opsForSet();
        zSetOperation = stringRedisTemplate.opsForZSet();
    }

    /**
     * String-set(设置键值对)
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringOperation.set(key, value, timeout, unit);
    }

    /**
     * String-set(如果key不存在则设置键值对)
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return key存在返回false, key不存在返回true
     */
    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        // set key value [timeout EX seconds|PX milliseconds] NX
        return stringOperation.setIfAbsent(key, value, timeout, unit);
    }

    /**
     * String-set(设置键值对,无过期时间)
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        stringOperation.set(key, value);
    }

    /**
     * String-get(获取指定key的值)
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return stringOperation.get(key);
    }

    /**
     * Hash-put
     */
    public void put(String key, String hashKey, String value) {
        hashOperation.put(key, hashKey, value);
    }

    /**
     * Hash-putAll
     */
    public void putAll(String key, Map<String, String> map) {
        hashOperation.putAll(key, map);
    }

    /**
     * Hash-get
     */
    public Object hashGet(String key, String hashKey) {
        return hashOperation.get(key, hashKey);
    }

    /**
     * 获取指定key对应的所有字段和值
     *
     * @param key key
     */
    public Map<String, String> entries(String key) {
        return hashOperation.entries(key);
    }

    /**
     * List-leftPush
     */
    public void leftPush(String key, String value) {
        listOperation.leftPush(key, value);
    }

    /**
     * List-leftPushAll
     */
    public void leftPushAll(String key, String... value) {
        listOperation.leftPushAll(key, value);
    }

    /**
     * List-rightPush
     */
    public void rightPush(String key, String value) {
        listOperation.rightPush(key, value);
    }

    /**
     * List-rightPushAll
     */
    public void rightPush(String key, String... value) {
        listOperation.rightPushAll(key, value);
    }

    /**
     * List-leftPop
     * List为空,直接返回null
     *
     * @param key key
     */
    public String leftPop(String key) {
        return listOperation.leftPop(key);
    }

    /**
     * List-BlockingLeftPop
     * List为空,阻塞等待
     *
     * @param key     key
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public String BlockingLeftPop(String key, long timeout, TimeUnit unit) {
        return listOperation.leftPop(key, timeout, unit);
    }

    /**
     * List-rightPop
     * List为空,直接返回null
     *
     * @param key key
     */
    public String rightPop(String key) {
        return listOperation.rightPop(key);
    }

    /**
     * List-BlockingRightPop
     * List为空,阻塞等待
     *
     * @param key     key
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public String BlockingRightPop(String key, long timeout, TimeUnit unit) {
        return listOperation.rightPop(key, timeout, unit);
    }

    /**
     * List-range
     * 取出所有元素
     *
     * @param key key
     */
    public List<String> range(String key) {
        // 0表示第一个元素,-1表示最后一个元素
        return listOperation.range(key, 0, -1);
    }

    /**
     * List-trim(删除所有元素)
     *
     * @param key key
     */
    public void trim(String key) {
        // 0表示第一个元素,-1表示最后一个元素
        listOperation.trim(key, 0, -1);
    }

    /**
     * Set-add(添加元素)
     *
     * @param key    键
     * @param values 值数组
     */
    public void setAdd(String key, String... values) {
        setOperation.add(key, values);
    }

    /**
     * Set-pop(移除并返回一个随机元素)
     *
     * @param key 键
     * @return 随机元素值
     */
    public Object setPop(String key) {
        // 如果Set为空,则返回null
        return setOperation.pop(key);
    }

    /**
     * Set-remove(移除指定key的一个或多个元素)
     *
     * @param key    键
     * @param values 值数组
     */
    public void remove(String key, Object... values) {
        setOperation.remove(key, values);
    }

    /**
     * Set-member(检查指定key的set中是否存在指定成员)
     *
     * @param key    键
     * @param member 成员
     * @return 存在返回true, 不存在返回false
     */
    public Boolean isMember(String key, String member) {
        return setOperation.isMember(key, member);
    }

    /**
     * ZSet-add
     */
    public void ZSetAdd(String key, String value, double score) {
        zSetOperation.add(key, value, score);
    }

    /**
     * ZSet-PopMin
     */
    public Object ZSetPopMin(String key) {
        return zSetOperation.popMin(key);
    }

    /**
     * ZSet-PopMax
     */
    public Object zSetPopMax(String key) {
        return zSetOperation.popMax(key);
    }

    /**
     * 删除指定的key
     *
     * @param key 键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 检查是否存在指定的key
     *
     * @param key 键
     * @return 存在返回true, 不存在返回false
     */
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }
}

