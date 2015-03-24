package moe.yuna.palinuridae.config;

import moe.yuna.palinuridae.condition.ConditionFormator;
import moe.yuna.palinuridae.condition.SimpleConditionFormator;
import moe.yuna.palinuridae.dialect.Dialect;
import moe.yuna.palinuridae.dialect.MysqlDialect;
import moe.yuna.palinuridae.xutilsmodel.SimpleXUtilsInfoFormator;
import moe.yuna.palinuridae.xutilsmodel.XUtilsInfoFormator;
import moe.yuna.palinuridae.xutilsmodel.cache.NoCacheImpl;
import moe.yuna.palinuridae.xutilsmodel.cache.XUtilsModelCache;
import org.springframework.context.annotation.Bean;

/**
 * Created by rika on 2015/1/14.
 */
public class FullConfig {

    @Bean
    public Dialect dialect() {
        return new MysqlDialect();
    }

    @Bean
    public DatabaseConfig databaseConfig() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setDialect(dialect());
        databaseConfig.setConditionFormator(conditionFormator());
        databaseConfig.setInfoFormatter(xutilsInfoFormatter());
        databaseConfig.setXutilsModelCache(xutilModelCache());
        return databaseConfig;
    }

    @Bean
    public ConditionFormator conditionFormator() {
        return new SimpleConditionFormator();
    }

    @Bean
    public XUtilsModelCache xutilModelCache() {
        return new NoCacheImpl();
    }

    @Bean
    public XUtilsInfoFormator xutilsInfoFormatter() {
        return new SimpleXUtilsInfoFormator();
    }

}
