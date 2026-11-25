package com.ems.api.service;

import com.ems.api.dto.EmployeeRequest;
import com.ems.api.dto.EmployeeResponse;
import com.ems.api.entity.Employee;
import com.ems.api.exception.ResourceNotFoundException;
import com.ems.api.mapper.EmployeeMapper;
import com.ems.api.repository.EmployeeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Lấy danh sách nhân viên
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    // Lấy nhân viên theo ID
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Không tìm thấy nhân viên ID: " + id
                    )
                );

        return EmployeeMapper.toResponse(employee);
    }

    // Thêm nhân viên
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = EmployeeMapper.toEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toResponse(savedEmployee);
    }

    // Cập nhật nhân viên
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Không tìm thấy nhân viên ID: " + id
                    )
                );

        existing.setFullName(request.getFullName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setPosition(request.getPosition());
        existing.setSalary(request.getSalary());

        Employee updated = employeeRepository.save(existing);
        return EmployeeMapper.toResponse(updated);
    }

    // Xóa nhân viên
    public void deleteEmployee(Long id) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Không tìm thấy nhân viên ID: " + id
                    )
                );

        employeeRepository.delete(existing);
    }

    // Tìm kiếm, lọc và phân trang nhân viên
    public Page<EmployeeResponse> searchEmployees(
            String keyword,
            String position,
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0) {
            throw new IllegalArgumentException("Số trang không được âm");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Kích thước trang phải từ 1 đến 100");
        }

        List<String> allowedFields = List.of(
            "id", "fullName", "email", "position", "salary"
        );

        if (!allowedFields.contains(sortBy)) {
            throw new IllegalArgumentException("Trường sắp xếp không hợp lệ");
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employees = employeeRepository
                .findByFullNameContainingIgnoreCaseAndPositionContainingIgnoreCase(
                    keyword == null ? "" : keyword.trim(),
                    position == null ? "" : position.trim(),
                    pageable
                );

        return employees.map(EmployeeMapper::toResponse);
    }
}