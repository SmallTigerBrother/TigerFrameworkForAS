package com.mn.tiger.datastorage.db.sqlite;

import android.text.TextUtils;

/**
 * 
 * 数据库查询条件类：提供链式语句方法，拼装为sql查询语句
 * 
 */
public class TGModelSelector {

    private String[] columnExpressions;
    private String groupByColumnName;
    private WhereBuilder having;

    private Selector selector;

    private TGModelSelector(Class<?> entityType) {
        selector = Selector.from(entityType);
    }

    protected TGModelSelector(Selector selector, String groupByColumnName) {
        this.selector = selector;
        this.groupByColumnName = groupByColumnName;
    }

    protected TGModelSelector(Selector selector, String[] columnExpressions) {
        this.selector = selector;
        this.columnExpressions = columnExpressions;
    }

    /**
     * 通过传入的类对象，构造DbModelSelector实例
     * @param entityType
     * @return
     * DbModelSelector
     */
    public static TGModelSelector from(Class<?> entityType) {
        return new TGModelSelector(entityType);
    }

    /**
     * 添加查询条件对象
     * @param whereBuilder
     * @return
     * DbModelSelector
     */
    public TGModelSelector where(WhereBuilder whereBuilder) {
        selector.where(whereBuilder);
        return this;
    }

    /**
     * 添加where查询条件
     * @param columnName 列名
     * @param op         运算符：“=、<、 >、=<、>=、between等”
     * @param value      查询数据
     * @return
     * DbModelSelector
     */
    public TGModelSelector where(String columnName, String op, Object value) {
        selector.where(columnName, op, value);
        return this;
    }

    /**
     * 添加and查询条件
     * @param columnName 列名
     * @param op         运算符：“=、<、 >、=<、>=、between等”
     * @param value      查询数据
     * @return
     * DbModelSelector
     */
    public TGModelSelector and(String columnName, String op, Object value) {
        selector.and(columnName, op, value);
        return this;
    }

    /**
     * 添加and查询条件对象
     * @param WhereBuilder 
     * @return
     * DbModelSelector
     */
    public TGModelSelector and(WhereBuilder where) {
        selector.and(where);
        return this;
    }

    /**
     * 添加or查询条件
     * @param columnName 列名
     * @param op         运算符：“=、<、 >、=<、>=、between等”
     * @param value      查询数据
     * @return
     * DbModelSelector
     */
    public TGModelSelector or(String columnName, String op, Object value) {
        selector.or(columnName, op, value);
        return this;
    }

    /**
     * 添加or查询条件对象
     * @param WhereBuilder 
     * @return
     * DbModelSelector
     */
    public TGModelSelector or(WhereBuilder where) {
        selector.or(where);
        return this;
    }

    /**
     * 添加正则查询条件
     * @param expr
     * @return
     * DbModelSelector
     */
    public TGModelSelector expr(String expr) {
        selector.expr(expr);
        return this;
    }

    public TGModelSelector expr(String columnName, String op, Object value) {
        selector.expr(columnName, op, value);
        return this;
    }

    public TGModelSelector groupBy(String columnName) {
        this.groupByColumnName = columnName;
        return this;
    }

    public TGModelSelector having(WhereBuilder whereBuilder) {
        this.having = whereBuilder;
        return this;
    }

    public TGModelSelector select(String... columnExpressions) {
        this.columnExpressions = columnExpressions;
        return this;
    }

    public TGModelSelector orderBy(String columnName) {
        selector.orderBy(columnName);
        return this;
    }

    public TGModelSelector orderBy(String columnName, boolean desc) {
        selector.orderBy(columnName, desc);
        return this;
    }

    public TGModelSelector limit(int limit) {
        selector.limit(limit);
        return this;
    }

    public TGModelSelector offset(int offset) {
        selector.offset(offset);
        return this;
    }

    public Class<?> getEntityType() {
        return selector.getEntityType();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("SELECT ");
        if (columnExpressions != null && columnExpressions.length > 0) {
            for (int i = 0; i < columnExpressions.length; i++) {
                result.append(columnExpressions[i]);
                result.append(",");
            }
            result.deleteCharAt(result.length() - 1);
        } else {
            if (!TextUtils.isEmpty(groupByColumnName)) {
                result.append(groupByColumnName);
            } else {
                result.append("*");
            }
        }
        result.append(" FROM ").append(selector.tableName);
        if (selector.whereBuilder != null && selector.whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ").append(selector.whereBuilder.toString());
        }
        if (!TextUtils.isEmpty(groupByColumnName)) {
            result.append(" GROUP BY ").append(groupByColumnName);
            if (having != null && having.getWhereItemSize() > 0) {
                result.append(" HAVING ").append(having.toString());
            }
        }
        if (selector.orderByList != null) {
            for (int i = 0; i < selector.orderByList.size(); i++) {
                result.append(" ORDER BY ").append(selector.orderByList.get(i).toString());
            }
        }
        if (selector.limit > 0) {
            result.append(" LIMIT ").append(selector.limit);
            result.append(" OFFSET ").append(selector.offset);
        }
        return result.toString();
    }
}
