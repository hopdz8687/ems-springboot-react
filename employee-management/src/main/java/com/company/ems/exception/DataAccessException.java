package com.company.ems.exception;

/** Lỗi truy cập cơ sở dữ liệu (bao SQLException để các tầng trên không phụ thuộc JDBC). */
public class DataAccessException extends EmsException {
    private static final long serialVersionUID = 1L;
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
