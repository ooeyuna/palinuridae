/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moe.yuna.test.base;

import moe.yuna.palinuridae.config.FullConfig;
import moe.yuna.palinuridae.core.SpringDbConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 *
 * @author rika
 */
@ContextConfiguration(classes = {FullConfig.class,SpringDbConfig.class})
public abstract class AbstractSpringTest extends AbstractJUnit4SpringContextTests{
    
    @Test
    public void test(){
        Assert.assertTrue(true);
    }
}
