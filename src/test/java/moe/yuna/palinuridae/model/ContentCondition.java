package moe.yuna.palinuridae.model;

import moe.yuna.palinuridae.condition.Condition;
import moe.yuna.palinuridae.condition.annotation.Expression;
import moe.yuna.palinuridae.condition.annotation.OrderBy;
import moe.yuna.palinuridae.xutilmodel.XUtilContent;

/**
 * Created by rika on 2015/1/14.
 */
public class ContentCondition extends XUtilContent implements Condition{

    int user_id;
    @Expression(operate = ">")
    int content_id;
    @OrderBy
    Integer __order;

    public void set__order(Integer __order) {
        this.__order = __order;
    }

    public String get__order() {
        if (__order == null) return "order by content_id desc";
        switch (__order) {
            case 1:
                return "order by content_id desc";
            case 2:
                return "order by content_id asc";
            case 3:
                return "order by views desc";
            default:
                return "order by content_id desc";
        }
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(Integer content_id) {
        this.content_id = content_id;
    }
}
