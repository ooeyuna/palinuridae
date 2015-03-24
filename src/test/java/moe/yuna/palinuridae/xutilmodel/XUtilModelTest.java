package moe.yuna.palinuridae.xutilmodel;

import moe.yuna.palinuridae.exception.DBUtilException;
import moe.yuna.palinuridae.model.ContentCondition;
import moe.yuna.palinuridae.utils.Pagination;
import moe.yuna.test.base.AbstractSpringTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by rika on 2015/1/14.
 */
public class XUtilModelTest extends AbstractSpringTest{
    @Autowired
    XUtilContentDao dao;

//    @Test
    public void testFindbyId() throws DBUtilException {
        XUtilContent c = dao.findById(41).get();
        Assert.assertEquals(4,c.getUser_id());
    }

//    @Test
    public void testCondition() throws DBUtilException {
        ContentCondition cc = new ContentCondition();
        cc.setContent_id(500_000);
        cc.setUser_id(271765);
        List<XUtilContent> select = dao.select(cc, 10, 1);

        List<Object> integers = dao.selectForPrimaryId(cc, 10, 1);

        Pagination pagination = dao.selectPage(cc, 10, 1);

        Pagination pagination1 = dao.selectPageForPrimaryId(cc, 10, 1);
    }
}
