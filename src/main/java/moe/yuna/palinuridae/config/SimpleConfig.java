package moe.yuna.palinuridae.config;

import moe.yuna.palinuridae.dialect.Dialect;
import moe.yuna.palinuridae.dialect.MysqlDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rika on 2015/1/9.
// */
public class SimpleConfig {
    @Bean
    public Dialect dialect() {
        return new MysqlDialect();
    }

    @Bean
    public DatabaseConfig databaseConfig(){
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setDialect(dialect());
        return databaseConfig;
    }

}
