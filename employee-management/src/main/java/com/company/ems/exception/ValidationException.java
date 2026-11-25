package com.company.ems.exception;

import java.util.List;

/** Dữ liệu đầu vào không hợp lệ; chứa toàn bộ danh sách lỗi để báo một lần. */
public class ValidationException extends EmsException {
    private static final long serialVersionUID = 1L;
    private final transient List<String> errors;

    public ValidationException(List<String> errors) {
        super("Dữ liệu không hợp lệ:\n - " + String.join("\n - ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() { return errors; }
}
