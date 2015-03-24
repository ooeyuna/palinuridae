package moe.yuna.palinuridae.xutilsmodel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rika on 2015/1/8.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XUtilsTable {

    public enum FreeColumnPolicy{
        Ignore,All,UnderlineUpper
    }

    //表名
    public String value();
    //缓存时间,0为无缓存
    public int cacheExpired() default 0;
    //缓存版本,用于实体类更新时重建缓存(变相清空缓存)
    public int cacheVersion() default 1;

    FreeColumnPolicy freeColumnPolicy() default FreeColumnPolicy.Ignore;

}
