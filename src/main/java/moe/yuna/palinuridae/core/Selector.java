package moe.yuna.palinuridae.core;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by rika on 2015/1/14.
 */
public class Selector {
    private String columns;
    private String tableName;
    private String whereExpression;
    private String orderBy;
    private List values;
    private Integer first;
    private Integer count;

    public Selector(String tableName){
        this.whereExpression = "";
        this.values = Lists.newArrayList();
        this.columns = " * ";
        this.tableName = tableName;
        this.orderBy = "";
    }

    public Selector(String tableName,String columns,String whereExpression, List values){
        this.whereExpression = whereExpression;
        this.values = values;
        this.columns = columns;
        this.tableName = tableName;
        this.orderBy = "";
    }

    public Selector(String tableName,String whereExpression){
        this.whereExpression = whereExpression;
        this.values = Lists.newArrayList();
        this.columns = " * ";
        this.tableName = tableName;
        this.orderBy = "";
    }

    public String getWhereExpression() {
        return whereExpression;
    }

    public void setWhereExpression(String whereExpression) {
        this.whereExpression = whereExpression;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "Selector{" +
                "whereExpression='" + whereExpression + '\'' +
                ", values=" + values +
                ", first=" + first +
                ", count=" + count +
                '}';
    }
}
