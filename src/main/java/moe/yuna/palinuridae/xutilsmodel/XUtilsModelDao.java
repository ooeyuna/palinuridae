package moe.yuna.palinuridae.xutilsmodel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moe.yuna.palinuridae.condition.Condition;
import moe.yuna.palinuridae.condition.ConditionFormator;
import moe.yuna.palinuridae.condition.exception.ConditionNotFoundException;
import moe.yuna.palinuridae.config.DatabaseConfig;
import moe.yuna.palinuridae.core.*;
import moe.yuna.palinuridae.dialect.Dialect;
import moe.yuna.palinuridae.exception.DBUtilException;
import moe.yuna.palinuridae.utils.Pagination;
import moe.yuna.palinuridae.xutilsmodel.cache.XUtilsModelCache;
import moe.yuna.palinuridae.xutilsmodel.exception.XUtilsModelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by rika on 2015/1/8.
 */
public abstract class XUtilsModelDao<T extends XUtilsModel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(XUtilsModelDao.class);

    /**
     * 从表中按数字主键取出所有字段数据并封装为T
     *
     * @param primaryValue 主键的值,注册于T类中@see
     * @return
     * @throws moe.yuna.palinuridae.exception.DBUtilException
     */
    public Optional<T> findById(Object primaryValue)
            throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            T bean = getXUtilsModelCache().get(primaryValue, getEntityClass());
            if (bean != null) {
                return Optional.of(bean);
            }
            String tableName = info.getTableInfo().getTableName();
            LOGGER.debug("EntityClass:" + getEntityClass().getName() + ",TableName:" + tableName);
            String sql = getDialect().findById(tableName, info.getXutilsId().getColumnName()
                    , getDialect().formatColumn(info.getTableInfo(), getXutilsInfoFormator().getColumnKeyOthernameValueMap(info)));
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + primaryValue);
            T b = getJdbcTemplate().queryForObject(sql, new ModelRowMapper(), primaryValue);
            getXUtilsModelCache().put(b);
            return Optional.of(b);
        } catch (NoSuchFieldException | DataAccessException | XUtilsModelNotFoundException ex) {
            if (ex instanceof EmptyResultDataAccessException) {
                return Optional.empty();
            }
            throw new DBUtilException(ex);
        }
    }

    public Optional<T> findByUnique(Map<String,Object> whereColumnValues)
            throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            List<String> whereColumns = Lists.newArrayList();
            List values = Lists.newArrayList();
            whereColumnValues.forEach((k,v)->{
                whereColumns.add(k);
                values.add(v);
            });
            LOGGER.debug("EntityClass:" + getEntityClass().getName() + ",TableName:" + tableName);
            String sql = getDialect().findByUnique(tableName, whereColumns
                    , getDialect().formatColumn(info.getTableInfo(), getXutilsInfoFormator().getColumnKeyOthernameValueMap(info)));
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + values);
            T b = getJdbcTemplate().queryForObject(sql, new ModelRowMapper(), values.toArray());
            getXUtilsModelCache().put(b);
            return Optional.of(b);
        } catch (NoSuchFieldException | DataAccessException | XUtilsModelNotFoundException ex) {
            if (ex instanceof EmptyResultDataAccessException) {
                return Optional.empty();
            }
            throw new DBUtilException(ex);
        }
    }

    public Optional<T> findByUnique(String field, Object value) throws DBUtilException {
        Map map = Maps.newHashMap();
        map.put(field,value);
        return findByUnique(map);
    }

    /**
     * @param sql
     * @param list
     * @param callback
     * @return
     * @throws DBUtilException
     */
    public List<T> select(final String sql,
                          final List<Object> list, final DBCallable<List<T>> callback)
            throws DBUtilException {
        LOGGER.debug("prepareSql:" + sql);
        LOGGER.debug("params:" + list);
        return getJdbcTemplate().query(sql, (stat) -> {
            for (int i = 0; i < list.size(); i++) {
                stat.setObject(i + 1, list.get(i));
            }
        }, callback);
    }

    /**
     * @param sql
     * @param list
     * @param callback
     * @return
     * @throws DBUtilException
     */
    public Pagination selectPage(final String sql,
                                 final List<Object> list, final DBCallable<Pagination> callback)
            throws DBUtilException {
        LOGGER.debug("prepareSql:" + sql);
        LOGGER.debug("params:" + list);
        return getJdbcTemplate().query(sql, (stat) -> {
            for (int i = 0; i < list.size(); i++) {
                stat.setObject(i + 1, list.get(i));
            }
        }, callback);
    }

    /**
     * @param count
     * @param first
     * @return
     * @throws DBUtilException
     */
    public List<T> select(final Condition condition, Integer count, Integer first) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            Selector selector = new Selector(tableName);
            getConditionFormat().format(condition, selector);
            selector.setColumns(getDialect().formatColumn(info.getTableInfo(),
                    getXutilsInfoFormator().getColumnKeyOthernameValueMap(info)));
            selector.setFirst(first);
            selector.setCount(count);
            String sql = getDialect().select(selector);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + selector.getValues());
            return getJdbcTemplate().query(sql, (stat) -> {
                for (int i = 0; i < selector.getValues().size(); i++) {
                    stat.setObject(i + 1, selector.getValues().get(i));
                }
            }, new ModelRowMapper());
        } catch (DataAccessException | XUtilsModelNotFoundException | ConditionNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public List<T> selectMapToModel(String sql, List<Object> values) {
        LOGGER.debug("prepareSql:" + sql);
        LOGGER.debug("params:" + values);
        return getJdbcTemplate().query(sql, (stat) -> {
            for (int i = 0; i < values.size(); i++) {
                stat.setObject(i + 1, values.get(i));
            }
        }, new ModelRowMapper());
    }

    public List<Object> selectForPrimaryId(final Condition condition,
                                           Integer count, Integer first) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            Selector selector = new Selector(tableName);
            getConditionFormat().format(condition, selector);
            selector.setColumns(getDialect().formatPrimaryIdColumn(info.getTableInfo()));
            selector.setFirst(first);
            selector.setCount(count);

            String sql = getDialect().select(selector);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + selector.getValues());
            return getJdbcTemplate().query(sql, (stat) -> {
                for (int i = 0; i < selector.getValues().size(); i++) {
                    stat.setObject(i + 1, selector.getValues().get(i));
                }
            }, (ResultSet rs) -> {
                List<Object> alist = new ArrayList();
                while (rs.next()) {
                    Object id = rs.getObject(1);
                    if (id != null) {
                        alist.add(id);
                    }
                }
                return alist;
            });
        } catch (XUtilsModelNotFoundException | DataAccessException | ConditionNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param pageSize
     * @param pageNo
     * @return
     * @throws DBUtilException
     */
    public Pagination selectPage(final Condition condition,
                                 int pageSize, int pageNo) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            int first = Pagination.formatFirst(pageNo, pageSize);
            int count = Pagination.formatCount(pageNo, pageSize);
            Selector selector = new Selector(tableName);
            getConditionFormat().format(condition, selector);
            selector.setColumns(getDialect().formatColumn(info.getTableInfo(),
                    getXutilsInfoFormator().getColumnKeyOthernameValueMap(info)));
            selector.setFirst(first);
            selector.setCount(count);

            String sql = getDialect().select(selector);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + selector.getValues());

            List<T> selectList = getJdbcTemplate().query(sql, (stat) -> {
                for (int i = 0; i < selector.getValues().size(); i++) {
                    stat.setObject(i + 1, selector.getValues().get(i));
                }
            }, new ModelRowMapper());

            int cou = getJdbcTemplate().query(getDialect().getCountSql(sql), (stat) -> {
                for (int i = 0; i < selector.getValues().size(); i++) {
                    stat.setObject(i + 1, selector.getValues().get(i));
                }
            }, (DBCallable<Integer>) (rs) -> {
                int c = 0;
                if (rs.next()) {
                    c = rs.getInt(1);
                }
                return c;
            });
            return new Pagination(pageNo, pageSize, cou, selectList);
        } catch (DataAccessException | XUtilsModelNotFoundException | ConditionNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public Pagination selectPageForPrimaryId(final Condition condition,
                                             int pageSize, int pageNo)
            throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            int first = Pagination.formatFirst(pageNo, pageSize);
            int count = Pagination.formatCount(pageNo, pageSize);
            Selector selector = new Selector(tableName);
            getConditionFormat().format(condition, selector);
            selector.setColumns(getDialect().formatPrimaryIdColumn(info.getTableInfo()));
            selector.setFirst(first);
            selector.setCount(count);

            String sql = getDialect().select(selector);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + selector.getValues());

            List<Object> selectForPrimaryId = getJdbcTemplate().query(sql, (stat) -> {
                for (int i = 0; i < selector.getValues().size(); i++) {
                    stat.setObject(i + 1, selector.getValues().get(i));
                }
            }, (ResultSet rs) -> {
                List<Object> alist = new ArrayList();
                while (rs.next()) {
                    Object id = rs.getObject(1);
                    if (id != null) {
                        alist.add(id);
                    }
                }
                return alist;
            });

            int cou = getJdbcTemplate().query(getDialect().getCountSql(sql), (stat) -> {
                for (int i = 0; i < selector.getValues().size(); i++) {
                    stat.setObject(i + 1, selector.getValues().get(i));
                }
            }, (DBCallable<Integer>) (rs) -> {
                int c = 0;
                if (rs.next()) {
                    c = rs.getInt(1);
                }
                return c;
            });
            return new Pagination(pageNo, pageSize, cou, selectForPrimaryId);
        } catch (XUtilsModelNotFoundException | DataAccessException | ConditionNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param wheresql
     * @param list
     * @param count
     * @param first
     * @return
     * @throws DBUtilException
     */
    public List<T> select(final String wheresql, final List<Object> list,
                          Integer count, Integer first) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            String sql = getDialect().select(tableName, getDialect().formatColumn(info.getTableInfo(),
                    getXutilsInfoFormator().getColumnKeyOthernameValueMap(info)), wheresql, first, count);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + list);

            return getJdbcTemplate().query(sql, (stat) -> {
                for (int i = 0; i < list.size(); i++) {
                    stat.setObject(i + 1, list.get(i));
                }
            }, new ModelRowMapper());
        } catch (DataAccessException | XUtilsModelNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public List<Object> selectForPrimaryId(final String wheresql, final List<Object> list,
                                           Integer count, Integer first) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            String sql = getDialect().select(tableName, getDialect().formatPrimaryIdColumn(info.getTableInfo()),
                    wheresql, first, count);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + list);
            return getJdbcTemplate().query(sql, (stat) -> {
                for (int i = 0; i < list.size(); i++) {
                    stat.setObject(i + 1, list.get(i));
                }
            }, (ResultSet rs) -> {
                List<Object> alist = new ArrayList();
                while (rs.next()) {
                    Object id = rs.getObject(1);
                    if (id != null) {
                        alist.add(id);
                    }
                }
                return alist;
            });
        } catch (XUtilsModelNotFoundException | DataAccessException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param wheresql
     * @param pageSize
     * @param pageNo
     * @param list
     * @return
     * @throws DBUtilException
     */
    public Pagination selectPage(final String wheresql, final List<Object> list,
                                 int pageSize, int pageNo) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            int first = Pagination.formatFirst(pageNo, pageSize);
            int count = Pagination.formatCount(pageNo, pageSize);
            String sql = getDialect().select(tableName
                    , getDialect().formatColumn(info.getTableInfo(), getXutilsInfoFormator().getColumnKeyOthernameValueMap(info))
                    , wheresql, first, count);
            List<T> selectList = select(wheresql, list, count, first);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + list);
            int cou = getJdbcTemplate().query(getDialect().getCountSql(sql), (stat) -> {
                for (int i = 0; i < list.size(); i++) {
                    stat.setObject(i + 1, list.get(i));
                }
            }, (DBCallable<Integer>) (rs) -> {
                int c = 0;
                if (rs.next()) {
                    c = rs.getInt(1);
                }
                return c;
            });
            return new Pagination(pageNo, pageSize, cou, selectList);
        } catch (DataAccessException | XUtilsModelNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public Pagination selectPageForPrimaryId(final String wheresql, final List<Object> list,
                                             int pageSize, int pageNo)
            throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            int first = Pagination.formatFirst(pageNo, pageSize);
            int count = Pagination.formatCount(pageNo, pageSize);
            String sql = getDialect().select(tableName, getDialect().formatPrimaryIdColumn(info.getTableInfo()),
                    wheresql, first, count);
            LOGGER.debug("prepareSql:" + sql);
            LOGGER.debug("params:" + list);
            List selectForPrimaryId = getJdbcTemplate().query(sql, (stat) -> {
                for (int i = 0; i < list.size(); i++) {
                    stat.setObject(i + 1, list.get(i));
                }
            }, (ResultSet rs) -> {
                List<Object> alist = new ArrayList();
                while (rs.next()) {
                    Object id = rs.getObject(1);
                    if (id != null) {
                        alist.add(id);
                    }
                }
                return alist;
            });
            int cou = getJdbcTemplate().query(getDialect().getCountSql(sql), (stat) -> {
                for (int i = 0; i < list.size(); i++) {
                    stat.setObject(i + 1, list.get(i));
                }
            }, (DBCallable<Integer>) (rs) -> {
                int c = 0;
                if (rs.next()) {
                    c = rs.getInt(1);
                }
                return c;
            });
            return new Pagination(pageNo, pageSize, cou, selectForPrimaryId);
        } catch (XUtilsModelNotFoundException | DataAccessException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param model
     * @throws DBUtilException
     */
    public void delete(T model) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            Object primaryValue = info.getXutilsId().getGetMethod().invoke(model);
            String sql = getDialect().delete(info.getTableInfo().getTableName(), info.getXutilsId().getColumnName());
            getJdbcTemplate().update(sql, (stat) -> {
                stat.setObject(1, primaryValue);
            });
            getXUtilsModelCache().remove(primaryValue, getEntityClass());
        } catch (XUtilsModelNotFoundException| IllegalAccessException |
                IllegalArgumentException | InvocationTargetException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param <T>
     * @param model
     * @return
     * @throws DBUtilException
     */
    public <T extends XUtilsModel> int updatePojo(T model) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            Map<String, Object> map = getXutilsInfoFormator().getFieldValue(model);
            final List<Object> paras = new ArrayList<>();
            String sql = getDialect().updatePojo(info.getTableInfo().getTableName(), map, info.getXutilsId().getColumnName(), paras);
            getXUtilsModelCache().put(model);
            return getJdbcTemplate().update(sql, (stat) -> {
                for (int i = 0; i < paras.size(); i++) {
                    stat.setObject(i + 1, paras.get(i));
                }
            });
        } catch (NoSuchFieldException | IllegalArgumentException | XUtilsModelNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    /**
     * @param columnValuePair
     * @param info
     * @return
     * @throws DBUtilException
     */
    private Object save(Map<String, Object> columnValuePair, TableInfo info)
            throws DBUtilException {
        try {
            if (info == null) {
                throw new DBUtilException("TableInfo Not Found!!!");
            }
            String tableName = info.getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableName Not Found!!!");
            }
            final List<Object> paras = new ArrayList<>();
            final String sql = getDialect().insert(tableName, columnValuePair, paras);
            LOGGER.debug("insert sql:" + sql);
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
     * @param <T>
     * @param model
     * @return
     * @throws DBUtilException
     */
    public <T extends XUtilsModel> Object save(T model) throws DBUtilException {
        try {
            //获得字段名和值,包括是否为空
            Map<String, Object> map = getXutilsInfoFormator().getFieldValue(model);
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            Object id = save(map, info.getTableInfo());
            return id;
        } catch (IllegalArgumentException | XUtilsModelNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public void updatePojoByUpdater(Updater<T> updater) throws DBUtilException {
        try {
            Map<String, Object> updateColumnValue = updater.getUpdateColumnValue();
            //map maybe empty or just have primary key,to say the bean is not update
            if (updateColumnValue.size() < 2) {
                return;
            }
            T model = updater.getAfterUpdateBean();
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableName Not Found!!!");
            }
            List paras = Lists.newArrayList();
            String sql = getDialect().updatePojo(info.getTableInfo().getTableName(), updateColumnValue, info.getXutilsId().getColumnName(), paras);
            getJdbcTemplate().update(sql, (stat) -> {
                for (int i = 0; i < paras.size(); i++) {
                    stat.setObject(i + 1, paras.get(i));
                }
            });
            Object primaryValue = info.getXutilsId().getGetMethod().invoke(model);
            getXUtilsModelCache().remove(primaryValue, getEntityClass());
        } catch (IllegalAccessException | IllegalArgumentException |
                InvocationTargetException | DBUtilException | XUtilsModelNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public void update(Map<String, Object> updateColumnValues, Condition whereCondition, Integer count) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableName Not Found!!!");
            }
            Selector selector = new Selector(tableName);
            List list = Lists.newArrayList();
            getConditionFormat().format(whereCondition, selector);
            String sql = getDialect().update(tableName, updateColumnValues, selector.getWhereExpression(), list, count);
            list.addAll(selector.getValues());
            getJdbcTemplate().update(sql, (stat) -> {
                for (int i = 0; i < list.size(); i++) {
                    stat.setObject(i + 1, list.get(i));
                }
            });
        } catch (ConditionNotFoundException | DBUtilException | XUtilsModelNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public void update(Map<String, Object> updateColumnValues, Object primaryValue) throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String tableName = info.getTableInfo().getTableName();
            if (tableName == null) {
                throw new DBUtilException("TableName Not Found!!!");
            }
            List list = Lists.newArrayList();
            String sql = getDialect().updatePojo(tableName, updateColumnValues, info.getXutilsId().getColumnName(), primaryValue, list);
            getJdbcTemplate().update(sql, (stat) -> {
                for (int i = 0; i < list.size(); i++) {
                    stat.setObject(i + 1, list.get(i));
                }
            });
        } catch (DBUtilException | XUtilsModelNotFoundException ex) {
            throw new DBUtilException(ex);
        }
    }

    public void evictCache(int primaryValue) throws DBUtilException {
        getXUtilsModelCache().remove(primaryValue, getEntityClass());
    }

    public String formatModelColumn() throws DBUtilException {
        try {
            XUtilsModelInfo info = XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass());
            String s = getDialect().formatColumn(info.getTableInfo(), getXutilsInfoFormator().getColumnKeyOthernameValueMap(info));
            return s;
        }catch(XUtilsModelNotFoundException ex){
            throw new DBUtilException(ex);
        }
    }

    class ModelRowMapper implements RowMapper<T> {

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            ResultSetMetaData md = rs.getMetaData();
            String columnName = null;
            try {
                T bean = getEntityClass().newInstance();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    columnName = md.getColumnLabel(i);
                    XUtilsModelPropertiesHolder.getXUtilsModelInfo(getEntityClass())
                            .getAllColumns()
                            .get(columnName)
                            .getSetMethod()
                            .invoke(bean, rs.getObject(i));
                }
                return bean;
            } catch (XUtilsModelNotFoundException | IllegalAccessException |
                    IllegalArgumentException | InstantiationException |
                    InvocationTargetException ex) {
                throw new SQLException("column:"+columnName,ex);
            }
        }

    }

    public abstract Class<T> getEntityClass();

    public XUtilsModelCache getXUtilsModelCache() {
        return config.getXutilsModelCache();
    }

    public XUtilsInfoFormator getXutilsInfoFormator() {
        return config.getInfoFormatter();
    }

    protected ConditionFormator getConditionFormat() {
        return config.getConditionFormator();
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

    private DataSource dataSource;

    @Autowired
    private DatabaseConfig config;
}
