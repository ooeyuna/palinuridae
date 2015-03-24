package moe.yuna.palinuridae.condition;

import com.google.common.collect.Lists;
import moe.yuna.palinuridae.condition.annotation.IgnoreCondition;
import moe.yuna.palinuridae.condition.annotation.OrderBy;
import moe.yuna.palinuridae.condition.exception.ConditionNotFoundException;
import moe.yuna.palinuridae.condition.exception.OrderByInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rika on 2015/1/14.
 */
public class ExpressionInfoHolder {

    private static final Logger log = LoggerFactory.getLogger(ExpressionInfoHolder.class);

    private static final ConcurrentHashMap<String, List<ExpressionInfo>> expressionInfos = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, OrderByInfo> orderByInfo = new ConcurrentHashMap<>();

    protected static ConcurrentHashMap<String, List<ExpressionInfo>> getExpressionInfos() {
        return expressionInfos;
    }

    public static synchronized void registerPojo(Class<? extends Condition> clazz) {
        List<ExpressionInfo> list = Lists.newArrayList();
        putClassField(clazz, Object.class, list);
        expressionInfos.put(clazz.getName(), list);
        log.info("ExpressionInfo:{} is register!", clazz.getName());
    }

    public static List<ExpressionInfo> getExpressions(Class<? extends Condition> clazz) throws ConditionNotFoundException {
        List<ExpressionInfo> list = expressionInfos.get(clazz.getName());
        if (list == null || list.isEmpty()) {
            registerPojo(clazz);
        }
        list = expressionInfos.get(clazz.getName());
        if (list == null || list.isEmpty()) {
            throw new ConditionNotFoundException("ExpressionInfo Not Found!!,clazz:" + clazz.getName());
        }
        return list;
    }

    public static Optional<OrderByInfo> getOrderByInfo(Class<? extends Condition> clazz) {
        OrderByInfo info = orderByInfo.get(clazz.getName());
        if (info == null) {
            registerPojo(clazz);
        }
        info = orderByInfo.get(clazz.getName());
        return Optional.ofNullable(info);
    }

    private static void putClassField(Class<?> superclass, Class stopClass, List<ExpressionInfo> list) {
        if (stopClass == null || superclass.equals(Object.class)) {
            return;
        }
        for (Field f : superclass.getDeclaredFields()) {
            if (f.getAnnotation(OrderBy.class) != null) {
                OrderByInfo info = new OrderByInfo(f, superclass);
                if (info.getGetMethod().getReturnType().equals(String.class)) {
                    orderByInfo.put(superclass.getName(), new OrderByInfo(f, superclass));
                } else {
                    log.warn("OrderBy Field:{} GetMethod isn't return String,Condition:{}", f.getName(), superclass.getName());
                }
            } else if (f.getAnnotation(IgnoreCondition.class) != null) {
                continue;
            } else {
                list.add(new ExpressionInfo(f, superclass));
            }
        }
        putClassField(superclass.getSuperclass(), stopClass, list);
    }

}
