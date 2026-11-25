
package com.ems.api.dto;

import java.math.BigDecimal;

public class EmployeeResponse {

    private Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    private String fullName;
    private String email;
    private String phone;
    private String position;
    private BigDecimal salary;

    public EmployeeResponse() {}

    public EmployeeResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            String position,
            BigDecimal salary) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.salary = salary;
    }

    // Generate Getter và Setter trong VS Code
}
