package moe.yuna.palinuridae.condition.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rika on 2015/1/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Expression {
    String value() default "";
    String operate() default "=";
    String expression() default "";
    int order() default 0;//The smaller the more front

}
