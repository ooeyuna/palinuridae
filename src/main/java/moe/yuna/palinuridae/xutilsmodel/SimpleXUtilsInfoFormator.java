package moe.yuna.palinuridae.xutilsmodel;

import moe.yuna.palinuridae.xutilsmodel.column.AbstractXUtilsColumn;
import moe.yuna.palinuridae.xutilsmodel.exception.XUtilsModelNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rika on 2015/1/8.
 */
public class SimpleXUtilsInfoFormator implements XUtilsInfoFormator {
    @Override
    public Map<String, String> getColumnKeyOthernameValueMap(XUtilsModelInfo info) {
        Map<String, AbstractXUtilsColumn> allColumns = info.getAllColumns();
        final Map<String, String> map = new HashMap<>();
        allColumns.forEach((k,v)->{
            map.put(v.getColumnName(),k);
        });
        return map;
    }

    @Override
    public <T extends XUtilsModel> Map<String, Object> getFieldValue(T bean) throws XUtilsModelNotFoundException {
        XUtilsModelInfo xUtilsModelInfo = XUtilsModelPropertiesHolder.getXUtilsModelInfo(bean.getClass());
        final Map<String, Object> map = new HashMap<>();
        xUtilsModelInfo.getAllColumns().forEach((k,v)->{
            try {
                map.put(v.getColumnName(),v.getGetMethod().invoke(bean));
            } catch (IllegalAccessException|InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
        return map;
    }
}
