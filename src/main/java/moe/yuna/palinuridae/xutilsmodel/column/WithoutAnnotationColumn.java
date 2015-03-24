package moe.yuna.palinuridae.xutilsmodel.column;

import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;

import java.lang.reflect.Field;

/**
 * Created by rika on 2015/1/8.
 */
public class WithoutAnnotationColumn extends AbstractXUtilsColumn{

    public WithoutAnnotationColumn(Class<? extends XUtilsModel> clazz, Field f, String columnName) {
        super(clazz, f, columnName);
    }

}
