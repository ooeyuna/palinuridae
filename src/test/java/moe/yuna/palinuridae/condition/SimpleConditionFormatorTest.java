package moe.yuna.palinuridae.condition;

import moe.yuna.palinuridae.condition.exception.ConditionNotFoundException;
import moe.yuna.palinuridae.core.Selector;
import moe.yuna.test.base.AbstractSpringTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by rika on 2015/1/14.
 */
public class SimpleConditionFormatorTest extends AbstractSpringTest {
    @Test
    public void testFormat() throws ConditionNotFoundException {
        TestCondition condition = new TestCondition();
        SimpleConditionFormator f = new SimpleConditionFormator();
        Selector selector = new Selector("1");

        f.format(condition, selector);
        Assert.assertEquals("", selector.getWhereExpression());

        condition.id = 10;
        f.format(condition, selector);
        Assert.assertEquals(" where `id`= ? ", selector.getWhereExpression());
        Assert.assertEquals(10, selector.getValues().get(0));

        condition.testId = 20;
        f.format(condition, selector);
        Assert.assertEquals(" where `id`= ?  and `testId`= ? ", selector.getWhereExpression());
        Assert.assertEquals(10, selector.getValues().get(0));
        Assert.assertEquals(20, selector.getValues().get(1));
    }

}
