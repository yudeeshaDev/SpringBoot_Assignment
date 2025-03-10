package com.springboot.assignment.controller;

import com.springboot.assignment.dto.EmployeeDTO;
import com.springboot.assignment.exception.DuplicateException;
import com.springboot.assignment.exception.ResourceNotFoundException;
import com.springboot.assignment.response.Response;
import com.springboot.assignment.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO createdEmployee = employeeService.create(employeeDTO);

            return Response.success(createdEmployee, "Employee created successfully.", HttpStatus.CREATED);
        } catch (DuplicateException e){
            return Response.error(e.getMessage(), HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            return Response.error("An error occurred while creating the employee - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO updatedEmployee = employeeService.update(id, employeeDTO);
            return Response.success(updatedEmployee, "Employee updated successfully.", HttpStatus.OK);
        } catch (DuplicateException e) {
            return Response.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e){
            return Response.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return Response.error("An error occurred while updating the employee - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> getById(@PathVariable Long id){
        try {
            EmployeeDTO employee = employeeService.getById(id);
            return Response.success(employee, "Employee retrieved successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException e){
            return Response.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return Response.error("An error occurred while retrieving the employee - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<EmployeeDTO> employees = employeeService.getAll(searchText, PageRequest.of(page, size));
            return Response.success(employees, "Employees retrieved successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return Response.error("An error occurred while retrieving the employees - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id){
        try{
            employeeService.deleteById(id);
            return Response.success(null, "Employee deleted successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException e){
            return Response.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return Response.error("An error occurred while deleting the employee - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
