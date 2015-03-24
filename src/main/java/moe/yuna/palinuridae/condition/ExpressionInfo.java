package moe.yuna.palinuridae.condition;

import moe.yuna.palinuridae.condition.annotation.Expression;
import moe.yuna.palinuridae.condition.annotation.Like;
import moe.yuna.palinuridae.core.AbstractFieldInfo;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * Created by rika on 2015/1/14.
 */
public class ExpressionInfo extends AbstractFieldInfo {

    private String expression;
    private int order;
    private boolean isAnnotation;

    public ExpressionInfo(Field field, Class<?> clazz) {
        super(field, clazz);
        Expression annotation = field.getAnnotation(Expression.class);
        if (annotation == null) {
            String operator;
            if (field.getAnnotation(Like.class) != null) {
                operator = "LIKE";
            }else{
                operator = "=";
            }
            expression = "`" + field.getName() + "`"+operator+" ? ";
            order = 0;
            isAnnotation = false;
        } else {
            order = annotation.order();
            isAnnotation = true;
            if (StringUtils.hasText(annotation.expression())) {
                expression = annotation.expression();
            } else {
                String operator;
                if (field.getAnnotation(Like.class) != null) {
                    operator = "LIKE";
                }else{
                    operator = annotation.operate();
                }
                expression = "`" + (StringUtils.hasText(annotation.value()) ? annotation.value() : field.getName()) + "`"
                        + operator + " ? ";
            }
        }
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isAnnotation() {
        return isAnnotation;
    }

    public void setAnnotation(boolean isAnnotation) {
        this.isAnnotation = isAnnotation;
    }

    @Override
    public String toString() {
        return "ExpressionInfo{" +
                "field=" + getField() +
                ", expression='" + expression + '\'' +
                ", order=" + order +
                ", isAnnotation=" + isAnnotation +
                ", getMehtod=" + getGetMethod() +
                '}';
    }
}
