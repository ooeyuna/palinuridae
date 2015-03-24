/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moe.yuna.palinuridae.dialect;

import moe.yuna.palinuridae.core.Selector;
import moe.yuna.palinuridae.core.TableInfo;

import java.util.List;
import java.util.Map;

/**
 * @author rika
 */
public class MysqlDialect implements Dialect {

    public String insert(String tableName, Map<String, Object> columnValueMap, List<Object> valueList) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `").append(tableName).append("` (");
        StringBuilder temp = new StringBuilder(") VALUES(");
        for (Map.Entry<String, Object> e : columnValueMap.entrySet()) {
            if (e.getValue() != null) {
                String colName = e.getKey();
                sb.append("`").append(colName).append("`");
                temp.append("?");
                sb.append(", ");
                temp.append(", ");
                valueList.add(e.getValue());
            }
        }
        sb.delete(sb.length() - 2, sb.length() - 1).append(temp.delete(temp.length() - 2, temp.length() - 1).toString()).append(")");
        return sb.toString();
    }

    @Override
    public String replace(String tableName, Map<String, Object> columnValueMap, List<Object> valueList) {
        StringBuilder sb = new StringBuilder();
        sb.append("REPLACE INTO `").append(tableName).append("` (");
        StringBuilder temp = new StringBuilder(") VALUES(");
        for (Map.Entry<String, Object> e : columnValueMap.entrySet()) {
            String colName = e.getKey();
            sb.append("`").append(colName).append("`");
            temp.append("?");
            sb.append(", ");
            temp.append(", ");
            valueList.add(e.getValue());
        }
        sb.delete(sb.length() - 2, sb.length() - 1).append(temp.delete(temp.length() - 2, temp.length() - 1).toString()).append(")");
        return sb.toString();
    }

    public String updatePojo(String tableName, Map<String, Object> columnValueMap, String primaryColumn, List<Object> valueList) {
        StringBuilder sb = new StringBuilder();
        Object primaryValue = columnValueMap.remove(primaryColumn);
        sb.append("UPDATE `").append(tableName).append("` set ");
        columnValueMap.entrySet().stream().forEach((e) -> {
            String colName = e.getKey();

            sb.append("`").append(colName).append("`=").append("?");
            sb.append(", ");
            valueList.add(e.getValue());
        });
        sb.delete(sb.length() - 2, sb.length() - 1)
                .append(" WHERE `").append(primaryColumn).append("`=?");
        valueList.add(primaryValue);
        return sb.toString();
    }

    @Override
    public String updatePojo(String tableName, Map<String, Object> columnValueMap, String primaryColumn, Object primaryValue, List<Object> valueList) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE `").append(tableName).append("` set ");
        columnValueMap.entrySet().stream().forEach((e) -> {
            String colName = e.getKey();
            sb.append("`").append(colName).append("`=").append("?");
            sb.append(", ");
            valueList.add(e.getValue());
        });
        sb.delete(sb.length() - 2, sb.length() - 1)
                .append(" WHERE `").append(primaryColumn).append("`=?");
        valueList.add(primaryValue);
        return sb.toString();
    }

    @Override
    public String update(String tableName, Map<String, Object> columnValueMap, String whereSql, List<Object> valueList, Integer count) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE `").append(tableName).append("` set ");
        columnValueMap.entrySet().stream().forEach((e) -> {
            String colName = e.getKey();
            sb.append("`").append(colName).append("`=").append("?");
            sb.append(", ");
            valueList.add(e.getValue());
        });
        sb.delete(sb.length() - 2, sb.length() - 1)
                .append(whereSql);
        if (count != null) {
            sb.append(" LIMIT ").append(count);
        }
        return sb.toString();
    }

    @Override
    public String delete(String tableName, String primaryColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM `").append(tableName)
                .append("` WHERE `").append(primaryColumn).append("`=? limit 1");
        return sb.toString();
    }

    @Override
    public String findById(String tableName, String primaryColumn,
                           String formatColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(formatColumn).append(" FROM `")
                .append(tableName).append("`").append(" WHERE ").append("`")
                .append(primaryColumn).append("`=? limit 1");
        return sb.toString();
    }

    @Override
    public String findByUnique(String tableName, List<String> whereColumns, String formatColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(formatColumn).append(" FROM `")
                .append(tableName).append("`").append(" WHERE ");
        whereColumns.forEach((e) -> {
            sb.append("`").append(e).append("`=").append("?");
            sb.append(" and ");
        });
        sb.delete(sb.length() - 4, sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String select(Selector selector) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(selector.getColumns()).append(" FROM `")
                .append(selector.getTableName()).append("`")
                .append(selector.getWhereExpression()).append(" ")
                .append(selector.getOrderBy());
        Integer first = selector.getFirst();
        Integer count = selector.getCount();
        if (first != null && count != null) {
            sb.append(" LIMIT ").append(first).append(",").append(count);
        } else if (count != null) {
            sb.append(" LIMIT ").append(count);
        }
        return sb.toString();
    }

    @Override
    public String select(String tableName, String formatColumn, String wheresql,
                         Integer first, Integer count) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(formatColumn).append(" FROM `")
                .append(tableName).append("`").append(" WHERE ")
                .append(wheresql);
        if (first != null && count != null) {
            sb.append(" LIMIT ").append(first).append(",").append(count);
        } else if (count != null) {
            sb.append(" LIMIT ").append(count);
        }
        return sb.toString();
    }

    @Override
    public String getSelect(TableInfo info, Map<String, String> columnKeyOthernameValueMap, String wheresql, Integer first, Integer count) {
        String tableName = info.getTableName();
        String sql = select(tableName, formatColumn(info, columnKeyOthernameValueMap),
                wheresql, first, count);
        return sql;
    }

    @Override
    public String getCountSql(String sql) {
        int fromIndex = sql.toLowerCase().indexOf(" from ");
        sql = sql.substring(fromIndex);
        String rowCountSql = sql.replace("fetch", "");
        int index = rowCountSql.indexOf("order by");
        if (index > 0) {
            rowCountSql = rowCountSql.substring(0, index);
        }

        int limitIndex = rowCountSql.indexOf("LIMIT");
        if (limitIndex > 0) {
            rowCountSql = rowCountSql.substring(0, limitIndex);
        }
        return "select count(*)" + rowCountSql;
    }

}
