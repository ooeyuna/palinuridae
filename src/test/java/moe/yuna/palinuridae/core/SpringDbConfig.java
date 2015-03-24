package moe.yuna.palinuridae.core;

import org.reindeer.redis.JedisHolder;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by rika on 2015/1/7.
 */
@Configuration
@ComponentScan(basePackages = {"moe.yuna.palinuridae", "org.reindeer"})
@PropertySource("classpath:dataSource1.properties")
@PropertySource("classpath:dataSource2.properties")
@PropertySource("classpath:redis.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringDbConfig {
    @Autowired
    Environment env;

    @Bean(destroyMethod = "close")
    public DruidDataSource dataSource() throws IOException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(env.getProperty("ds1.mysql.driverClass"));
        dataSource.setUrl(env.getProperty("ds1.mysql.jdbcUrl"));
        dataSource.setUsername(env.getProperty("ds1.mysql.user"));
        dataSource.setPassword(env.getProperty("ds1.mysql.password"));
        dataSource.setMinIdle(env.getProperty("ds1.mysql.minPoolSize", Integer.class));
        dataSource.setInitialSize(env.getProperty("ds1.mysql.minPoolSize", Integer.class));
        dataSource.setMaxActive(env.getProperty("ds1.mysql.maxPoolSize", Integer.class));
        dataSource.setMaxWait(env.getProperty("ds1.mysql.maxIdleTime", Long.class));
        dataSource.setTestWhileIdle(true);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws IOException {
        return new JdbcTemplate(dataSource(), false);
    }

    @Bean
    public DefaultFormattingConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

    @Bean
    public redis.clients.jedis.JedisPoolConfig jedisPoolFactoryConfig() {
        redis.clients.jedis.JedisPoolConfig config = new redis.clients.jedis.JedisPoolConfig();
        config.setMaxIdle(env.getProperty("common_redis.maxIdle", Integer.class));
        config.setMaxTotal(env.getProperty("common_redis.maxActive", Integer.class));
        config.setMaxWaitMillis(env.getProperty("common_redis.maxWait", Long.class));
        config.setMinIdle(env.getProperty("common_redis.minIdle", Integer.class));
        return config;
    }

    @Bean
    public JedisShardInfo jedisShardInfo1() {
        return new JedisShardInfo(env.getProperty("redis_cache1.ip"), env.getProperty("redis_cache1.port", Integer.class));
    }

    @Bean
    public JedisShardInfo jedisShardInfo2() {
        return new JedisShardInfo(env.getProperty("redis_cache2.ip"), env.getProperty("redis_cache2.port", Integer.class));
    }

    @Bean
    public ShardedJedisPool shardedJedisPool() {
        return new ShardedJedisPool(jedisPoolFactoryConfig(), Arrays.asList(jedisShardInfo1(), jedisShardInfo2()));
    }

    @Bean
    public JedisHolder jedisHolder() {
        JedisHolder jedisHolder = new JedisHolder();
        jedisHolder.setShardedJedisPool(shardedJedisPool());
        return jedisHolder;
    }


}
