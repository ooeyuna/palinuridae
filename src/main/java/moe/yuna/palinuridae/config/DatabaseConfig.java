package moe.yuna.palinuridae.config;

import moe.yuna.palinuridae.condition.ConditionFormator;
import moe.yuna.palinuridae.dialect.Dialect;
import moe.yuna.palinuridae.xutilsmodel.XUtilsInfoFormator;
import moe.yuna.palinuridae.xutilsmodel.cache.XUtilsModelCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by rika on 2015/1/8.
 */
public class DatabaseConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    private Dialect dialect;
    private XUtilsModelCache xutilsModelCache;
    private XUtilsInfoFormator infoFormatter;
    private ConditionFormator conditionFormator;

    public XUtilsModelCache getXutilsModelCache() {
        return xutilsModelCache;
    }

    public void setXutilsModelCache(XUtilsModelCache xutilsModelCache) {
        this.xutilsModelCache = xutilsModelCache;
    }

    public XUtilsInfoFormator getInfoFormatter() {
        return infoFormatter;
    }

    public void setInfoFormatter(XUtilsInfoFormator infoFormatter) {
        this.infoFormatter = infoFormatter;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public JdbcTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ConditionFormator getConditionFormator() {
        return conditionFormator;
    }

    public void setConditionFormator(ConditionFormator conditionFormator) {
        this.conditionFormator = conditionFormator;
    }

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JdbcTemplate template;
    @Autowired
    private DataSource dataSource;


}
