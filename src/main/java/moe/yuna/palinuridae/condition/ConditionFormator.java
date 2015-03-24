package moe.yuna.palinuridae.condition;

import moe.yuna.palinuridae.condition.exception.ConditionNotFoundException;
import moe.yuna.palinuridae.core.Selector;

/**
 * Created by rika on 2015/1/14.
 */
public interface ConditionFormator {

    public <T extends Condition> Selector format(T condition, Selector selector) throws ConditionNotFoundException;
}
