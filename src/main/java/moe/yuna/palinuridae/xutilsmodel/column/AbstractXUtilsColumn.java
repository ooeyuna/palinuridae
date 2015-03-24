package moe.yuna.palinuridae.xutilsmodel.column;

import moe.yuna.palinuridae.core.AbstractFieldInfo;
import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;

import java.lang.reflect.Field;

/**
 * Created by rika on 2015/1/8.
 */
public abstract class AbstractXUtilsColumn extends AbstractFieldInfo{

    private String fieldName;
    private String columnName;

    public AbstractXUtilsColumn(Class<? extends XUtilsModel> clazz,Field f, String columnName){
        super(f,clazz);
        this.fieldName = f.getName();
        this.columnName = columnName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public int hashCode() {
        int result = fieldName.hashCode();
        result = 31 * result + columnName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AbstractXUtilsColumn{" +
                "fieldName='" + fieldName + '\'' +
                ", columnName='" + columnName + '\'' +
                "} " + super.toString();
    }
}
