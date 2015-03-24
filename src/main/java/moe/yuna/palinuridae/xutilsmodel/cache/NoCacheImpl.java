package moe.yuna.palinuridae.xutilsmodel.cache;

import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;

/**
 * Created by rika on 2015/1/8.
 */
public class NoCacheImpl implements XUtilsModelCache{
    @Override
    public <T extends XUtilsModel> void put(T value) throws NoSuchFieldException {

    }

    @Override
    public <T extends XUtilsModel> T get(Object primaryValue, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends XUtilsModel> void remove(Object primaryValue, Class<T> clazz) {

    }
}
