package moe.yuna.palinuridae.xutilsmodel;

import moe.yuna.palinuridae.xutilsmodel.exception.XUtilsModelNotFoundException;

import java.util.Map;

/**
 * Created by rika on 2015/1/8.
 */
public interface XUtilsInfoFormator {
    Map<String,String> getColumnKeyOthernameValueMap(XUtilsModelInfo info);

    <T extends XUtilsModel> Map<String,Object>  getFieldValue(T bean) throws XUtilsModelNotFoundException;
}
