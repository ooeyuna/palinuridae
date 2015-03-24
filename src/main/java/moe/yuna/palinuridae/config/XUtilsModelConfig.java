package moe.yuna.palinuridae.config;

import moe.yuna.palinuridae.condition.ConditionFormator;
import moe.yuna.palinuridae.condition.SimpleConditionFormator;
import moe.yuna.palinuridae.dialect.Dialect;
import moe.yuna.palinuridae.dialect.MysqlDialect;
import moe.yuna.palinuridae.xutilsmodel.SimpleXUtilsInfoFormator;
import moe.yuna.palinuridae.xutilsmodel.XUtilsInfoFormator;
import moe.yuna.palinuridae.xutilsmodel.cache.NoCacheImpl;
import moe.yuna.palinuridae.xutilsmodel.cache.XUtilsModelCache;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rika on 2015/1/9.
 */
public class XUtilsModelConfig {

    @Bean
    public Dialect dialect() {
        return new MysqlDialect();
    }

    @Bean
    public DatabaseConfig databaseConfig() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setXutilsModelCache(xutilModelCache());
        databaseConfig.setInfoFormatter(xutilsInfoFormatter());
        databaseConfig.setDialect(dialect());
        databaseConfig.setConditionFormator(conditionFormator());
        return databaseConfig;
    }

    @Bean
    public XUtilsModelCache xutilModelCache() {
        return new NoCacheImpl();
    }

    @Bean
    public XUtilsInfoFormator xutilsInfoFormatter() {
        return new SimpleXUtilsInfoFormator();
    }
    @Bean
    public ConditionFormator conditionFormator() {
        return new SimpleConditionFormator();
    }

}
