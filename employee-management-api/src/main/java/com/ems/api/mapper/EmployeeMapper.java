
package com.ems.api.mapper;

import com.ems.api.dto.EmployeeRequest;
import com.ems.api.dto.EmployeeResponse;
import com.ems.api.entity.Employee;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();

        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());

        return employee;
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
            employee.getId(),
            employee.getFullName(),
            employee.getEmail(),
            employee.getPhone(),
            employee.getPosition(),
            employee.getSalary()
        );
    }
}
