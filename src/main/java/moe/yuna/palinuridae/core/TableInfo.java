/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moe.yuna.palinuridae.core;

/**
 * @author rika
 */
public class TableInfo {
    private String tableName;
    private String primaryColumn;
    private int cacheExpired;
    private int cacheVersion;

    public TableInfo() {
    }

    public String getPrimaryColumn() {
        return primaryColumn;
    }

    public void setPrimaryColumn(String primaryColumn) {
        this.primaryColumn = primaryColumn;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getCacheExpired() {
        return cacheExpired;
    }

    public void setCacheExpired(int cacheExpired) {
        this.cacheExpired = cacheExpired;
    }

    public int getCacheVersion() {
        return cacheVersion;
    }

    public void setCacheVersion(int cacheVersion) {
        this.cacheVersion = cacheVersion;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "tableName='" + tableName + '\'' +
                ", primaryColumn='" + primaryColumn + '\'' +
                ", cacheExpired=" + cacheExpired +
                ", cacheVersion=" + cacheVersion +
                '}';
    }
}
