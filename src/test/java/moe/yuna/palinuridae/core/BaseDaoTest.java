/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moe.yuna.palinuridae.core;

import moe.yuna.palinuridae.exception.DBUtilException;
import moe.yuna.test.base.AbstractSpringTest;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author rika
 */
public class BaseDaoTest extends AbstractSpringTest{
    
    @Autowired
    private BaseDao dao;
    @Autowired
    private DataSource dataSource2;
    
//    @Test
    public void testMultiDataSource() throws DBUtilException{
        Map<String, Object> map = dao.findById("jc_content", "content_id", 41).get();
        assertEquals(4, (int)map.get("user_id"));
        
        dao.setDataSource(dataSource2);
        Map<String, Object> map2 = dao.findById("ac_content_comment", "id", 800).get();
        assertEquals(41, (int)map2.get("content_id"));
        
        dao.setDataSource(BaseDao.DEFAULT_DATASOURCE);
        Map<String, Object> map3 = dao.findById("jc_content", "content_id", 41).get();
        assertEquals(4, (int)map3.get("user_id"));

    }

//    @Test
    public void testSave() throws DBUtilException {
        dao.delete("jo_upload","filename","test");
        Map<String,Object> smap = new HashMap<>();
        smap.put("filename","test");
        smap.put("length",123);
        smap.put("last_modified",System.currentTimeMillis());
        smap.put("content","keshuimeidingding");
        dao.save(smap,"jo_upload");
        Map<String,Object> ssmap = new HashMap<>();
        ssmap.put("site_id",1);
        ssmap.put("recommendctg_name","123321");
        ssmap.put("priority",10);
        ssmap.put("recommendctg_subname","testttt");
        Object id = dao.save(ssmap,"jc_recommend_ctg");
        Assert.assertNotEquals(0,id);
    }

    @Test
    public void testFindByUnique() throws DBUtilException{

    }
}
