package moe.yuna.palinuridae.xutilsmodel;

import moe.yuna.palinuridae.core.TableInfo;
import moe.yuna.palinuridae.utils.PojoAnnotationUtil;
import moe.yuna.palinuridae.xutilsmodel.annotation.*;
import moe.yuna.palinuridae.xutilsmodel.column.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rika on 2015/1/8.
 */
public class XUtilsModelInfo {


    private TableInfo tableInfo;
    private XUtilsId xutilsId;
    private Map<String, XUtilsForeign> foreigns;
    private Map<String, XUtilsColumn> otherColumns;
    private Map<String, AbstractXUtilsColumn> allColumns;
    private Map<String, WithoutAnnotationColumn> withoutAnnotationColumnMap;

    public XUtilsModelInfo(Class<? extends XUtilsModel> clazz) {
        foreigns = new HashMap<>();
        otherColumns = new HashMap<>();
        allColumns = new HashMap<>();
        withoutAnnotationColumnMap = new HashMap<>();

        XUtilsTable anno = clazz.getAnnotation(XUtilsTable.class);
        putClassField(clazz, Object.class, anno);
        tableInfo = new TableInfo();
        tableInfo.setCacheExpired(anno.cacheExpired());
        tableInfo.setCacheVersion(anno.cacheVersion());
        tableInfo.setTableName(anno.value());
        tableInfo.setPrimaryColumn(xutilsId.getColumnName());
    }

    private void putClassField(Class<?> superclass, Class stopClass, XUtilsTable anno) {
        if (stopClass == null || superclass.equals(Object.class)) {
            return;
        }
        for (Field f : superclass.getDeclaredFields()) {
            if(Modifier.isStatic(f.getModifiers())){
                continue;
            }
            if (f.getAnnotation(Column.class) != null) {
                putColumn(superclass, f);
                continue;
            }
            if (f.getAnnotation(Id.class) != null) {
                putId(superclass, f);
                continue;
            }
            if (f.getAnnotation(Foreign.class) != null) {
                putForeign(superclass, f);
                continue;
            }
            if (f.getAnnotation(IgnoreColumn.class) != null) {
                continue;
            }
            putWithoutAnnotationColumnMap(superclass, f, anno.freeColumnPolicy());
        }
        putClassField(superclass.getSuperclass(), stopClass, anno);
    }

    private void putId(Class clazz, Field field) {
        XUtilsId id = new XUtilsId(clazz, field);
        xutilsId = id;
        putIntoAllColumnMap(id);
    }

    private void putColumn(Class clazz, Field field) {
        XUtilsColumn column = new XUtilsColumn(clazz, field);
        otherColumns.put(column.getFieldName(), column);
        putIntoAllColumnMap(column);
    }

    private void putForeign(Class clazz, Field field) {
        XUtilsForeign foreign = new XUtilsForeign(clazz, field);
        foreigns.put(foreign.getFieldName(), foreign);
        putIntoAllColumnMap(foreign);
    }

    private void putWithoutAnnotationColumnMap(Class clazz, Field field, XUtilsTable.FreeColumnPolicy policy) {
        String columnName = null;
        switch (policy) {
            case Ignore:
                break;
            case All:
                columnName = field.getName();
                break;
            case UnderlineUpper:
                columnName = PojoAnnotationUtil.convertColumn(field.getName());
                break;
            default:
                break;
        }
        if (columnName != null) {
            WithoutAnnotationColumn wac = new WithoutAnnotationColumn(clazz, field, columnName);
            withoutAnnotationColumnMap.put(wac.getFieldName(), wac);
            putIntoAllColumnMap(wac);
        }
    }

    private void putIntoAllColumnMap(AbstractXUtilsColumn column) {
        allColumns.put(column.getFieldName(), column);
    }

    public Map<String, XUtilsForeign> getForeigns() {
        return foreigns;
    }

    public Map<String, XUtilsColumn> getOtherColumns() {
        return otherColumns;
    }

    public Map<String, AbstractXUtilsColumn> getAllColumns() {
        return allColumns;
    }

    public Map<String, WithoutAnnotationColumn> getWithoutAnnotationColumnMap() {
        return withoutAnnotationColumnMap;
    }

    public XUtilsId getXutilsId() {
        return xutilsId;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }
}
