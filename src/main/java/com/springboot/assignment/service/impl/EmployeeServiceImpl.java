package com.springboot.assignment.service.impl;

import com.springboot.assignment.dto.EmployeeDTO;
import com.springboot.assignment.exception.DuplicateException;
import com.springboot.assignment.exception.ResourceNotFoundException;
import com.springboot.assignment.model.Employee;
import com.springboot.assignment.repository.EmployeeRepository;
import com.springboot.assignment.service.EmployeeService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    //employee create method
    @Override
    public EmployeeDTO create(EmployeeDTO employeeDTO) {
        validateEmployeeFields(employeeDTO); // Validate fields before proceeding

        //check if the email already exists
        if(employeeRepository.existsByEmployeeEmail(employeeDTO.getEmployeeEmail())){
            throw new DuplicateException("Email " + employeeDTO.getEmployeeEmail() + " is already taken");
        }

        //convert Dto to entity
        Employee employee = convertToEntity(employeeDTO);

        //save the employee to the database
        Employee savedEmployee = employeeRepository.save(employee);

        //convert the saved entity back to dto and return it
        return convertToDto(savedEmployee);
    }

    //employee update method
    @Override
    public EmployeeDTO update(Long id, EmployeeDTO employeeDTO) {
        validateEmployeeFields(employeeDTO); // Validate fields before proceeding

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        //check if the email already exists for another employee, excluding the same person
        if(!existingEmployee.getEmployeeEmail().equals(employeeDTO.getEmployeeEmail()) &&
                employeeRepository.existsByEmployeeEmail(employeeDTO.getEmployeeEmail())) {
            throw new DuplicateException("Email " + employeeDTO.getEmployeeEmail() + " is already taken");
        }

        // Convert DTO to Entity but ensure ID is preserved
        Employee updatedEmployee = convertToEntity(employeeDTO);

        updatedEmployee.setId(id);  // Ensure ID remains unchanged

        // Save and return the updated entity
        return convertToDto(employeeRepository.save(updatedEmployee));

    }

    //retrive employee by id method
    @Override
    public EmployeeDTO getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return convertToDto(employee);
    }

    //retrieve all employees method
    @Override
    public Page<EmployeeDTO> getAll(String searchText, PageRequest pageRequest) {
        Specification<Employee> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(searchText != null && !searchText.trim().isEmpty()){
                String searchPattern = "%" + searchText.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("employeeName")), searchPattern),
                        cb.like(cb.lower(root.get("employeeDepartment")), searchPattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return employeeRepository.findAll(spec, pageRequest).map(this::convertToDto);
    }

    //delete employee by id method
    @Override
    public void deleteById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " +  id));
        employeeRepository.delete(employee);
    }

    protected EmployeeDTO convertToDto(Employee entity){
        return modelMapper.map(entity, EmployeeDTO.class);
    }

    protected Employee convertToEntity(EmployeeDTO dto){
        return modelMapper.map(dto, Employee.class);
    }

    //helper method to validate eidate dto feilds
    private void validateEmployeeFields(EmployeeDTO employeeDTO){
        if (employeeDTO.getEmployeeName() == null || employeeDTO.getEmployeeName().trim().isEmpty()){
            throw new IllegalArgumentException("Employee name is required.");
        }
        if (employeeDTO.getEmployeeEmail() == null || employeeDTO.getEmployeeEmail().trim().isEmpty()){
            throw new IllegalArgumentException("Employee email is required.");
        }
        if (employeeDTO.getEmployeeDepartment() == null || employeeDTO.getEmployeeDepartment().trim().isEmpty()){
            throw new IllegalArgumentException("Employee department is required.");
        }
    }
}
