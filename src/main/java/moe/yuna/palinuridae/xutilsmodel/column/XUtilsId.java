package moe.yuna.palinuridae.xutilsmodel.column;

import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;
import moe.yuna.palinuridae.xutilsmodel.annotation.Id;

import java.lang.reflect.Field;

/**
 * Created by rika on 2015/1/8.
 */
public class XUtilsId extends AbstractXUtilsColumn {

    public XUtilsId(Class<? extends XUtilsModel> clazz, Field f, String columnName) {
        super(clazz, f, columnName);
    }

    public XUtilsId(Class<? extends XUtilsModel> clazz,Field f){
        super(clazz,f,f.getAnnotation(Id.class).value());
    }
}
