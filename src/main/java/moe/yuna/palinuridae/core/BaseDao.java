/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moe.yuna.palinuridae.core;

import moe.yuna.palinuridae.config.DatabaseConfig;
import moe.yuna.palinuridae.dialect.Dialect;
import moe.yuna.palinuridae.exception.DBUtilException;
import moe.yuna.palinuridae.utils.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

/**
 * @author rika
 */
@Component
public class BaseDao {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final DataSource DEFAULT_DATASOURCE = null;

    /**
     * 无参数构造函数,使用默认datasource
     */
    public BaseDao() {

    }

    /**
     * @param tableName
     * @param primaryColumn
     * @param primaryValue
     * @throws DBUtilException
     */
    public void delete(String tableName, String primaryColumn, Object primaryValue)
            throws DBUtilException {
        try {
            String sql = getDialect().delete(tableName, primaryColumn);
            getJdbcTemplate().update(sql, (stat) -> {
                stat.setObject(1, primaryValue);
            });
        } catch (DataAccessException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * 用sql來執行增刪改操作
     *
     * @param prepareSql prepare sql语句
     * @param list       参数列表
     * @throws DBUtilException
     */
    public void updateOrDelete(final String prepareSql,
                               final List<Object> list) throws DBUtilException {
        getJdbcTemplate().update(prepareSql, (stat) -> {
            for (int i = 0; i < list.size(); i++) {
                stat.setObject(i + 1, list.get(i));
            }
        });
    }

    /**
     * 从表中按数字主键取出所有字段数据并封装为Map,key为字段名,value为值
     *
     * @param tableName     表名
     * @param primaryColumn 主键字段名
     * @param primaryValue  主键值
     * @return
     * @throws DBUtilException
     */
    public Optional<Map<String, Object>> findById(String tableName, String primaryColumn,
                                                  Object primaryValue) throws DBUtilException {
        try {
            Map<String, Object> columnValuePair = new HashMap<>();
            columnValuePair.put(primaryColumn, primaryValue);
            String sql = getDialect().findById(tableName, primaryColumn, "*");
            return Optional.ofNullable(getJdbcTemplate().queryForMap(sql, primaryValue));
        } catch (DataAccessException ex) {
            if (ex instanceof EmptyResultDataAccessException) {
                return Optional.empty();
            }
            throw new DBUtilException(ex);
        }
    }

    public Optional<Map<String, Object>> findByUnique(String tableName, List<String> uniqueColumn,
                                                      List values) throws DBUtilException {
        try {
            String sql = getDialect().findByUnique(tableName, uniqueColumn, "*");
            return Optional.ofNullable(getJdbcTemplate().queryForMap(sql, values));
        } catch (DataAccessException ex) {
            if (ex instanceof EmptyResultDataAccessException) {
                return Optional.empty();
            }
            throw new DBUtilException(ex);
        }
    }

    public Optional<Map<String, Object>> findByUnique(String tableName, String uniqueColumn,
                                                      Object value) throws DBUtilException {
        return findByUnique(tableName, Arrays.asList(uniqueColumn), Arrays.asList(value));
    }

    public List<Map<String, Object>> select(final Selector selector) throws DBUtilException {
        return getJdbcTemplate().query(getDialect().select(selector), (stat) -> {
            for (int i = 0; i < selector.getValues().size(); i++) {
                stat.setObject(i + 1, selector.getValues().get(i));
            }
        }, (DBCallable<List<Map<String, Object>>>) (rs) -> {
            ResultSetMetaData md = rs.getMetaData();
            List<Map<String, Object>> rlist = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    map.put(md.getColumnName(i), rs.getObject(i));
                    map.put(md.getColumnLabel(i), rs.getObject(i));
                }
                rlist.add(map);
            }
            return rlist;
        });
    }

    /**
     * @param sql
     * @param list
     * @return
     * @throws DBUtilException
     */
    public List<Map<String, Object>> select(final String sql,
                                            final List<Object> list) throws DBUtilException {
        return getJdbcTemplate().query(sql, (stat) -> {
            for (int i = 0; i < list.size(); i++) {
                stat.setObject(i + 1, list.get(i));
            }
        }, (DBCallable<List<Map<String, Object>>>) (rs) -> {
            ResultSetMetaData md = rs.getMetaData();
            List<Map<String, Object>> rlist = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    map.put(md.getColumnName(i), rs.getObject(i));
                    map.put(md.getColumnLabel(i), rs.getObject(i));
                }
                rlist.add(map);
            }
            return rlist;
        });
    }

    public Pagination selectPage(final String sql,
                                 final List<Object> list, final int pageSize, final int pageNo) throws DBUtilException {
        List<Map<String, Object>> resultList = select(getDialect().appendPageInfo(sql, pageNo, pageSize), list);
        int count = getJdbcTemplate().query(getDialect().getCountSql(sql), (stat) -> {
            for (int i = 0; i < list.size(); i++) {
                stat.setObject(i + 1, list.get(i));
            }
        }, (DBCallable<Integer>) (rs) -> {
            int cou = 0;
            if (rs.next()) {
                cou = rs.getInt(1);
            }
            return cou;
        });
        return new Pagination(pageNo, pageSize, count, resultList);
    }

    /**
     * @param columnValuePair
     * @param tableName
     * @return
     * @throws DBUtilException
     */
    public Object save(Map<String, Object> columnValuePair, String tableName)
            throws DBUtilException {
        try {
            final List<Object> paras = new ArrayList<>();
            final String sql = getDialect().insert(tableName, columnValuePair, paras);
            log.debug("insert sql:" + sql);
            int id = 0;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update((PrepareStatementCreator) (conn) -> {
                PreparedStatement stat = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0, size = paras.size(); i < size; i++) {
                    stat.setObject(i + 1, paras.get(i));
                }

                return stat;
            }, keyHolder);
            Number key = keyHolder.getKey();
            id = key != null ? key.intValue() : 0;
            return id;
        } catch (DataAccessException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param columnValuePair
     * @param tableName
     * @return
     * @throws DBUtilException
     */
    public void replace(Map<String, Object> columnValuePair, String tableName)
            throws DBUtilException {
        try {
            final List<Object> paras = new ArrayList<>();
            final String sql = getDialect().replace(tableName, columnValuePair, paras);
            log.debug("replace sql:" + sql);
            getJdbcTemplate().update(sql, (stat) -> {
                for (int i = 0, size = paras.size(); i < size; i++) {
                    stat.setObject(i + 1, paras.get(i));
                }
            });
        } catch (DataAccessException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param columnValuePair
     * @param tableName
     * @param primaryColumn
     * @return
     * @throws DBUtilException
     */
    public int updatePojo(Map<String, Object> columnValuePair, String tableName,
                          String primaryColumn) throws DBUtilException {
        try {
            final List<Object> paras = new ArrayList<>();
            String sql = getDialect().updatePojo(tableName, columnValuePair, primaryColumn, paras);
            return getJdbcTemplate().update(sql, (stat) -> {
                for (int i = 0; i < paras.size(); i++) {
                    stat.setObject(i + 1, paras.get(i));
                }
            });
        } catch (DataAccessException ex) {
            throw new DBUtilException(ex);
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        if (dataSource != null) {
            return new JdbcTemplate(dataSource);
        }
        return config.getTemplate();
    }

    public Dialect getDialect() {
        return config.getDialect();
    }

    public DataSource getDataSource() {
        return config.getDataSource();
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    private DatabaseConfig config;

    private DataSource dataSource;
}
