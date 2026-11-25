package com.company.ems.exception;

/** Lỗi nghiệp vụ (không tìm thấy, vi phạm quy tắc...). Thông điệp hiển thị trực tiếp cho người dùng. */
public class EmsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public EmsException(String message) { super(message); }
    public EmsException(String message, Throwable cause) { super(message, cause); }
}
