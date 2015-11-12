package com.mn.tiger.datastorage.db.exception;

/**
 * 
 * 自定义数据库异常类
 * 
 */
public class DbException extends BaseException {
    private static final long serialVersionUID = 1L;

    public DbException() {
    }

    public DbException(String detailMessage) {
        super(detailMessage);
    }

    public DbException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DbException(Throwable throwable) {
        super(throwable);
    }
}
