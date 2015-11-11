package com.mn.tiger.datastorage.db;

import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.datastorage.db.upgrade.AbsDbUpgrade;

/**
 * 数据库配置类
 * 用于配置数据库名称、版本信息、升级策略等
 * @since 2014年2月18日
 */
public class DaoConfig {
	private Context context;
    private String dbName = "tiger.db"; // default db name
    private int dbVersion = 1;
    private com.mn.tiger.datastorage.db.upgrade.AbsDbUpgrade dbUpgrade;

    private String dbDir;

    public DaoConfig(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        if (!TextUtils.isEmpty(dbName)) {
            this.dbName = dbName;
        }
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getDbDir() {
        return dbDir;
    }

    /**
     * set database dir
     *
     * @param dbDir If dbDir is null or empty, use the app default db dir.
     */
    public void setDbDir(String dbDir) {
        this.dbDir = dbDir;
    }

	/**
	 * @return the dbUpgrade
	 */
	public AbsDbUpgrade getDbUpgrade() {
		return dbUpgrade;
	}

	/**
	 * @param dbUpgrade the dbUpgrade to set
	 */
	public void setDbUpgrade(AbsDbUpgrade dbUpgrade) {
		this.dbUpgrade = dbUpgrade;
	}
}
