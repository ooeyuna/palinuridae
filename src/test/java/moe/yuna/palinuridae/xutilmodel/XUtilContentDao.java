package moe.yuna.palinuridae.xutilmodel;

import moe.yuna.palinuridae.xutilsmodel.XUtilsModelDao;
import org.springframework.stereotype.Component;

/**
 * Created by rika on 2015/1/14.
 */
@Component
public class XUtilContentDao extends XUtilsModelDao<XUtilContent> {

    @Override
    public Class<XUtilContent> getEntityClass() {
        return XUtilContent.class;
    }
}
