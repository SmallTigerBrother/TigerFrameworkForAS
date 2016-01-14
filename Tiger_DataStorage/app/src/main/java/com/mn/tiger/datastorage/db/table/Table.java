package com.mn.tiger.datastorage.db.table;

import android.text.TextUtils;

import com.mn.tiger.datastorage.TGDBManager;

import java.util.HashMap;
import java.util.Map;


public class Table {

    private String tableName;

    private Id id;

    /**
     * key: columnName
     */
    public final HashMap<String, Column> columnMap;

    /**
     * key: dbName#className
     */
    private static final HashMap<String, Table> tableMap = new HashMap<String, Table>();

    private Table(Class<?> entityType) {
        this.tableName = TableUtils.getTableName(entityType);
        this.id = TableUtils.getId(entityType);
        this.columnMap = TableUtils.getColumnMap(entityType);
    }

    public static synchronized Table get(TGDBManager db, Class<?> entityType) {
        String tableKey = db.getDaoConfig().getDbName() + "#" + entityType.getCanonicalName();
        Table table = tableMap.get(tableKey);
        if (table == null) {
            table = new Table(entityType);
            tableMap.put(tableKey, table);
        }

        return table;
    }

    public static synchronized void remove(TGDBManager db, Class<?> entityType) {
        String tableKey = db.getDaoConfig().getDbName() + "#" + entityType.getCanonicalName();
        tableMap.remove(tableKey);
    }

    public static synchronized void remove(TGDBManager db, String tableName) {
        if (tableMap.size() > 0) {
            String key = null;
            for (Map.Entry<String, Table> entry : tableMap.entrySet()) {
                Table table = entry.getValue();
                if (table != null && table.getTableName().equals(tableName)) {
                    key = entry.getKey();
                    if (key.startsWith(db.getDaoConfig().getDbName() + "#")) {
                        break;
                    }
                }
            }
            if (TextUtils.isEmpty(key)) {
                tableMap.remove(key);
            }
        }
    }

    public String getTableName() {
        return tableName;
    }

    public Id getId() {
        return id;
    }

    private boolean checkedDatabase;

    public boolean isCheckedDatabase() {
        return checkedDatabase;
    }

    public void setCheckedDatabase(boolean checkedDatabase) {
        this.checkedDatabase = checkedDatabase;
    }

}
