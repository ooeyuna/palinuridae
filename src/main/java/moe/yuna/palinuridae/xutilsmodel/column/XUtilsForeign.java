package moe.yuna.palinuridae.xutilsmodel.column;

import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;
import moe.yuna.palinuridae.xutilsmodel.annotation.Foreign;

import java.lang.reflect.Field;

/**
 * Created by rika on 2015/1/8.
 */
public class XUtilsForeign extends AbstractXUtilsColumn{
    public XUtilsForeign(Class<? extends XUtilsModel> clazz, Field f, String columnName) {
        super(clazz, f, columnName);
    }

    public XUtilsForeign(Class<? extends XUtilsModel> clazz,Field f){
        super(clazz,f,f.getAnnotation(Foreign.class).value());
    }
}
