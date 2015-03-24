/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moe.yuna.palinuridae.xutilsmodel.cache;

import org.reindeer.redis.Redis;
import moe.yuna.palinuridae.xutilsmodel.*;
import moe.yuna.palinuridae.xutilsmodel.column.AbstractXUtilsColumn;
import moe.yuna.palinuridae.xutilsmodel.exception.XUtilsModelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import redis.clients.jedis.ShardedJedis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author rika
 */
public class XUtilsRedisCacheImpl implements XUtilsModelCache {

    private static final String CACHE_COUNT_MAP_KEY = "cache:count:map";
    private static final String CACHE_MISS_COUNT_KEY = "cache:miss:count";
    private static final String MODEL_CACHE_MISS_COUNT_KEY = "model:name:%s:cache:miss:count";
    private static final String CACHE_HIT_COUNT_KEY = "cache:hit:count";
    private static final String MODEL_CACHE_HIT_COUNT_KEY = "model:name:%s:cache:hit:count";
    private static final String CACHE_USE_COUNT_KEY = "cache:use:count";
    private static final String MODEL_CACHE_USE_COUNT_KEY = "model:name:%s:cache:use:count";
    private static final Logger log = LoggerFactory.getLogger(XUtilsRedisCacheImpl.class);

    @Override
    @Redis(shard = true)
    public <T extends XUtilsModel> void put(T value) {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(value.getClass());
            if (info.getTableInfo().getCacheExpired() <= 0) {
                log.debug("model:" + value.getClass().getSimpleName() + " not cache");
                return;
            }
            int seconds = info.getTableInfo().getCacheExpired();
            Map<String, Object> map = infoFomatter.getFieldValue(value);
            String key = formatCacheKey(value.getClass().getName(), info.getXutilsId().getGetMethod().invoke(value)
                    , info.getTableInfo().getCacheVersion());
            Map<String, String> valueMap = new HashMap<>();
            map.keySet().stream().filter((s) -> (map.get(s) != null)).forEach((s) -> {
                Object v = map.get(s);
                if (v instanceof Date) {
                    valueMap.put(s, String.valueOf(((Date) v).getTime()));
                } else {
                    valueMap.put(s, String.valueOf(map.get(s)));
                }
            });
            jedis.hmset(key, valueMap);
            jedis.expire(key, seconds);
            log.debug("put cache key:" + key + ",value:" + value.toString() + ",expired:" + seconds);
        } catch (XUtilsModelNotFoundException|IllegalAccessException|InvocationTargetException e) {
            log.error("",e);
        }
    }

    @Override
    @Redis(shard = true)
    public <T extends XUtilsModel> T get(Object primaryValue, Class<T> clazz) {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(clazz);
            if (info.getTableInfo().getCacheExpired() <= 0) {
                log.debug("model:" + clazz.getSimpleName() + " not cache");
                return null;
            }
            Map<String, String> hgetAll = null;
            String key = formatCacheKey(clazz.getName(), primaryValue, info.getTableInfo().getCacheVersion());
            hgetAll = jedis.hgetAll(key);
            if (hgetAll == null || hgetAll.isEmpty()) {
                jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(CACHE_MISS_COUNT_KEY), 1);
                jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(MODEL_CACHE_MISS_COUNT_KEY, clazz.getName()), 1);
                jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(CACHE_USE_COUNT_KEY), 1);
                jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(MODEL_CACHE_USE_COUNT_KEY, clazz.getName()), 1);
                log.debug("miss cache!!key:" + key);
                return null;
            }
            jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(CACHE_HIT_COUNT_KEY), 1);
            jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(MODEL_CACHE_HIT_COUNT_KEY, clazz.getName()), 1);
            jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(CACHE_HIT_COUNT_KEY), 1);
            jedis.hincrBy(CACHE_COUNT_MAP_KEY, String.format(MODEL_CACHE_HIT_COUNT_KEY, clazz.getName()), 1);
            log.debug("hit cache!!key:" + key + ",value:" + hgetAll.toString());
            T bean = clazz.newInstance();
            for (Entry<String, String> entry : hgetAll.entrySet()) {
                AbstractXUtilsColumn columnInfo = info.getAllColumns().get(entry.getKey());
                Field field = columnInfo.getField();
                columnInfo.getSetMethod().invoke(bean, formatOjbect(field, entry.getValue()));
            }
            return bean;
        } catch (IllegalArgumentException | IllegalAccessException |
                InstantiationException |InvocationTargetException|XUtilsModelNotFoundException ex) {
            log.error("params:" + primaryValue + ",Class:" + clazz.getName(), ex);
            return null;
        }
    }

    @Override
    @Redis(shard = true)
    public <T extends XUtilsModel> void remove(Object primaryValue, Class<T> clazz) {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(clazz);
            String key = formatCacheKey(clazz.getName(), primaryValue, info.getTableInfo().getCacheVersion());
            jedis.del(key);
            log.debug("remove cache!!key:" + key);
        } catch (XUtilsModelNotFoundException e) {
            log.error("params:" + primaryValue + ",Class:" + clazz.getName(), e);
        }
    }

    private static final String MODEL_CACHE_KEY = "xutilsmodel:name:%s:cache:id:%s:version:%s:hashmap";

    private String formatCacheKey(String modelname, Object primaryValue, int version) {
        String key = String.format(MODEL_CACHE_KEY, modelname, primaryValue,version);
        return key;
    }

    public Object formatOjbect(Field field, String object) {
        if (field.getType().equals(Date.class)) {
            return new Date(Long.valueOf(object));
        }
        Object convert = conversionService.convert(object, field.getType());
        return convert;
    }

    @Autowired
    private ConversionService conversionService;
    @Autowired
    private ShardedJedis jedis;
    @Autowired
    private XUtilsInfoFormator infoFomatter;

}
