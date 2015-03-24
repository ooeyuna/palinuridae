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

import moe.yuna.palinuridae.utils.Pagination;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rika
 */
public interface Dialect {

    public String insert(String tableName, Map<String, Object> columnValueMap,
            List<Object> valueList);

    public String replace(String tableName, Map<String, Object> columnValueMap,
                         List<Object> valueList);

    public String updatePojo(String tableName, Map<String, Object> columnValueMap,
                             String primaryColumn, List<Object> valueList);

    public String updatePojo(String tableName, Map<String, Object> columnValueMap,
                             String primaryColumn, Object primaryValue, List<Object> valueList);

    public String update(String tableName, Map<String,Object> columnValueMap,String whereSql,List<Object> valueList,Integer count);

    public String delete(String tableName, String primaryColumn);
    public String findById(String tableName, String primaryColumn, 
            String formatColumn);

    public String findByUnique(String tableName,List<String> whereColumns, String formatColumn);

    public String select(Selector selector);

    public String select(String tableName,String formatColumn, String wheresql,
            Integer first, Integer count);
    
    public String getSelect(TableInfo info, Map<String,String> columnKeyOthernameValueMap, String wheresql, Integer first,
			Integer count);
    
    public String getCountSql(String sql);
    
    default public String appendPageInfo(String sql, int pageNo, int pageSize){
         return sql + " LIMIT " + Pagination.formatFirst(pageNo, pageSize) + "," 
                 + Pagination.formatCount(pageNo, pageSize);

    }

    default public String formatPrimaryIdColumn(TableInfo info, String otherName) {
        return new StringBuilder().append(" `").append(info.getTableName()).append("`.`")
                .append(otherName).append("` AS ").append(info.getPrimaryColumn()).append(" ,").toString();
    }

    default public String formatPrimaryIdColumn(TableInfo info) {
        return " `"+info.getPrimaryColumn()+"` ";
    }

    default public String formatColumn(TableInfo info, Map<String,String> columnKeyOthernameValueMap){
        StringBuilder sb = new StringBuilder();
        LoggerFactory.getLogger(Dialect.class).debug(columnKeyOthernameValueMap.toString());
        columnKeyOthernameValueMap.forEach((key,value)->{
            sb.append(" `").append(info.getTableName()).append("`.`").append(key).append("` AS ").append(value).append(" ,");
        });
        
        return sb.delete(sb.length()-1, sb.length()).toString();
    }

}
