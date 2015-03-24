/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moe.yuna.palinuridae.xutilsmodel.cache;


import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;

/**
 *
 * @author rika
 */
public interface XUtilsModelCache {
    public <T extends XUtilsModel> void put(T value) throws NoSuchFieldException;
    
    public <T extends XUtilsModel> T get(Object primaryValue, Class<T> clazz);
        
    public <T extends XUtilsModel> void remove(Object primaryValue, Class<T> clazz);

}
