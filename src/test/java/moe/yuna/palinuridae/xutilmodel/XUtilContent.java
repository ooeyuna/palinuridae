package moe.yuna.palinuridae.xutilmodel;

import moe.yuna.palinuridae.xutilsmodel.XUtilsModel;
import moe.yuna.palinuridae.xutilsmodel.annotation.Id;
import moe.yuna.palinuridae.xutilsmodel.annotation.XUtilsTable;

/**
 * Created by rika on 2015/1/14.
 */
@XUtilsTable(value = "jc_content", freeColumnPolicy = XUtilsTable.FreeColumnPolicy.All)
public class XUtilContent extends XUtilsModel {
    int user_id;
    @Id("content_id")
    int content_id;

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
