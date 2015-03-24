package moe.yuna.palinuridae.xutilsmodel.column;

import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;
import moe.yuna.palinuridae.xutilsmodel.annotation.Column;

import java.lang.reflect.Field;

/**
 * Created by rika on 2015/1/8.
 */
public class XUtilsColumn extends AbstractXUtilsColumn {

    private boolean readOnly = false;
    public XUtilsColumn(Class<? extends XUtilsModel> clazz, Field f, String columnName) {
        super(clazz, f, columnName);
        this.readOnly = f.getAnnotation(Column.class).readOnly();
    }

    public XUtilsColumn(Class<? extends XUtilsModel> clazz,Field f){
        super(clazz,f,f.getAnnotation(Column.class).value());
        this.readOnly = f.getAnnotation(Column.class).readOnly();
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
