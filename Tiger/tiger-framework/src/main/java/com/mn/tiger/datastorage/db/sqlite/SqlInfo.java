package com.mn.tiger.datastorage.db.sqlite;

import java.util.LinkedList;

import com.mn.tiger.datastorage.db.table.ColumnUtils;

public class SqlInfo {

    private String sql;
    private LinkedList<Object> bindArgs;

    public SqlInfo() {
    }

    public SqlInfo(String sql) {
        this.sql = sql;
    }

    public SqlInfo(String sql, Object... bindArgs) {
        this.sql = sql;
        addBindArgs(bindArgs);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public LinkedList<Object> getBindArgs() {
        return bindArgs;
    }

    public Object[] getBindArgsAsArray() {
        if (bindArgs != null) {
            return bindArgs.toArray();
        }
        return null;
    }

    public String[] getBindArgsAsStrArray() {
        if (bindArgs != null) {
            String[] strings = new String[bindArgs.size()];
            Object value = null;
            for (int i = 0; i < bindArgs.size(); i++) {
                value = bindArgs.get(i);
                strings[i] = value == null ? null : value.toString();
            }
            return strings;
        }
        return null;
    }

    public void addBindArg(Object arg) {
        if (bindArgs == null) {
            bindArgs = new LinkedList<Object>();
        }

        bindArgs.add(ColumnUtils.convert2DbColumnValueIfNeeded(arg));
    }
    
    public void addBindArgWithoutConverter(Object arg) {
        if (bindArgs == null) {
            bindArgs = new LinkedList<Object>();
        }

        bindArgs.add(arg);
    }

    public void addBindArgs(Object... bindArgs) {
        if (bindArgs != null) {
            for (Object arg : bindArgs) {
                addBindArg(arg);
            }
        }
    }

}
