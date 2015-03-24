package moe.yuna.palinuridae.condition;

import moe.yuna.palinuridae.condition.annotation.Expression;
import moe.yuna.palinuridae.condition.annotation.OrderBy;

/**
 * Created by rika on 2015/1/14.
 */
public class TestCondition implements Condition {
    @Expression(order = 2)
    Integer testId;
    @Expression(order = 1)
    Integer id;
    @OrderBy
    Integer order;

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrder() {
        if (order == null) return "order by content_id desc";
        switch (order) {
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

    public void setOrder(Integer order) {
        this.order = order;
    }
}
