package moe.yuna.palinuridae.core;

import moe.yuna.palinuridae.utils.PojoAnnotationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by rika on 2015/1/15.
 */
public class AbstractFieldInfo {

    private Field field;

    private Method getMethod;
    private Method setMethod;

    public AbstractFieldInfo(Field field, Class<?> clazz) {
        this.field = field;
        this.getMethod = BeanUtils.findMethod(clazz, PojoAnnotationUtil.getFieldGetMethodString(field));
        this.setMethod = BeanUtils.findMethod(clazz, PojoAnnotationUtil.getFieldSetMethodString(field), field.getType());
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }

    @Override
    public String toString() {
        return "AbstractFieldInfo{" +
                "field=" + field +
                ", getMethod=" + getMethod +
                ", setMethod=" + setMethod +
                '}';
    }
}
