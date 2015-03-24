package moe.yuna.palinuridae.condition.exception;

import moe.yuna.palinuridae.core.AbstractFieldInfo;

import java.lang.reflect.Field;

/**
 * Created by rika on 2015/1/15.
 */
public class OrderByInfo extends AbstractFieldInfo {

    public OrderByInfo(Field field, Class<?> clazz) {
        super(field, clazz);
    }
}
