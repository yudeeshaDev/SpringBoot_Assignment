package com.springboot.assignment.service;

import com.springboot.assignment.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {

    EmployeeDTO create(EmployeeDTO employeeDTO);
    EmployeeDTO update(Long id, EmployeeDTO employeeDTO);
    EmployeeDTO getById(Long id);
    Page<EmployeeDTO> getAll(String searchText, PageRequest pageRequest);
    void deleteById(Long id);
}
