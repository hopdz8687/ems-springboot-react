
package com.ems.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class EmployeeRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(
        regexp = "^$|^[0-9]{10,11}$",
        message = "Số điện thoại phải có 10 hoặc 11 chữ số"
    )
    private String phone;

    @NotBlank(message = "Chức vụ không được để trống")
    private String position;

    @NotNull(message = "Lương không được để trống")
    @DecimalMin(value = "0.0")
    private BigDecimal salary;

    public EmployeeRequest() {}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    // Generate Getter và Setter trong VS Code
}

