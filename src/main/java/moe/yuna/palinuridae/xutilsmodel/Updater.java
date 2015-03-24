/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moe.yuna.palinuridae.xutilsmodel;

import moe.yuna.palinuridae.exception.DBUtilException;
import moe.yuna.palinuridae.xutilsmodel.column.AbstractXUtilsColumn;
import moe.yuna.palinuridae.xutilsmodel.column.XUtilsColumn;
import moe.yuna.palinuridae.xutilsmodel.column.XUtilsId;
import moe.yuna.palinuridae.xutilsmodel.exception.XUtilsModelNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @param <T>
 * @author rika
 */
public class Updater<T extends XUtilsModel> {

    private T beforeUpdateBean;
    private T afterUpdateBean;

    public static <T extends XUtilsModel> Updater newInstance(T beforeUpdateBean)
            throws CloneNotSupportedException {
        return new Updater(beforeUpdateBean);
    }

    /**
     * @param bean
     * @throws CloneNotSupportedException
     */
    private Updater(T bean) throws CloneNotSupportedException {
        this.beforeUpdateBean = (T) bean.clone();
        afterUpdateBean = bean;
    }

    private Updater(T before, T after){
        this.beforeUpdateBean = before;
        this.afterUpdateBean = after;
    }

    /**
     * @return
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public Map<String, Object> getUpdateColumnValue() throws DBUtilException, XUtilsModelNotFoundException,
            InvocationTargetException, IllegalAccessException {
        if (beforeUpdateBean == null) {
            throw new DBUtilException("Updater Init Error!!");
        }
        XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(beforeUpdateBean.getClass());
        HashMap<String, Object> changeMap = new HashMap();
        for (Entry<String, AbstractXUtilsColumn> entry : info.getAllColumns().entrySet()) {
            AbstractXUtilsColumn columnInfo = entry.getValue();
            //if column is primary key, put in map
            if (columnInfo instanceof XUtilsId) {
                changeMap.put(columnInfo.getColumnName(), columnInfo.getGetMethod().invoke(afterUpdateBean));
            } else if (columnInfo instanceof XUtilsColumn && ((XUtilsColumn) columnInfo).getReadOnly()) {
                continue;
            } else {
                //if column is change,put in map
                Object before = columnInfo.getGetMethod().invoke(beforeUpdateBean);
                Object after = columnInfo.getGetMethod().invoke(afterUpdateBean);
                //no change
                if (after == null) {
                    continue;
                } else if (before == null && after != null) {
                    changeMap.put(columnInfo.getColumnName(), after);
                } else if (!before.equals(after)) {
                    changeMap.put(columnInfo.getColumnName(), after);
                }
            }
        }
        return changeMap;
    }

    public static <T extends XUtilsModel> void getUpdateColumnValue(T before, T after, XUtilsModelDao dao)
            throws DBUtilException, XUtilsModelNotFoundException, CloneNotSupportedException,InvocationTargetException, IllegalAccessException {
        Updater<T> updater = new Updater<>(before,after);
        dao.updatePojoByUpdater(updater);
    }

    public T getAfterUpdateBean() {
        return afterUpdateBean;
    }
}
