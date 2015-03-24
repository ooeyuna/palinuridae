package moe.yuna.palinuridae.xutilsmodel;

import moe.yuna.palinuridae.xutilsmodel.exception.XUtilsModelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rika on 2015/1/8.
 */
public class XUtilsModelPropertiesHolder {

    private static final Logger log = LoggerFactory.getLogger(XUtilsModelPropertiesHolder.class);

    private static final ConcurrentHashMap<String, XUtilsModelInfo> xutilsModelInfoMap = new ConcurrentHashMap<>();

    protected static ConcurrentHashMap<String, XUtilsModelInfo> getXutilsModelInfoMap() {
        return xutilsModelInfoMap;
    }

    public static synchronized void registerPojo(Class<? extends XUtilsModel> clazz) {
        XUtilsModelInfo info = new XUtilsModelInfo(clazz);
        xutilsModelInfoMap.put(clazz.getName(),info);
        log.info("Model:{} is register!",clazz.getName());
    }

    public static XUtilsModelInfo getXUtilsModelInfo(Class<? extends XUtilsModel> clazz) throws XUtilsModelNotFoundException {
        XUtilsModelInfo xUtilsModelInfo = xutilsModelInfoMap.get(clazz.getName());
        if(xUtilsModelInfo == null){
            registerPojo(clazz);
        }
        xUtilsModelInfo = xutilsModelInfoMap.get(clazz.getName());
        if(xUtilsModelInfo == null){
            throw new XUtilsModelNotFoundException("XUtilsModel Not Found!!,clazz:"+clazz.getName());
        }
        return xUtilsModelInfo;
    }

}
