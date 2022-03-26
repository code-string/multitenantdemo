package com.example.multitenantdemo.controller;

import com.example.multitenantdemo.domain.Employee;
import com.example.multitenantdemo.exception.NotFoundException;
import com.example.multitenantdemo.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    public List<Employee> get(){
        return employeeService.getAll();
    }

    @PostMapping()
    public Employee save(@RequestBody Employee employee){
        return employeeService.save(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable("id") String id, @RequestBody Employee employee){
        Employee theEmployee = employeeService.getById(id).orElseThrow(() -> new NotFoundException(String.format("Employee with id %s not found", id)));

        theEmployee.setEmail(employee.getEmail());
        theEmployee.setFirstName(employee.getFirstName());
        theEmployee.setLastName(employee.getLastName());
        return employeeService.save(theEmployee);
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable("id") String id){
        return employeeService.getById(id).orElseThrow(() -> new NotFoundException(String.format("Employee with id %s not found", id)));
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable("id") String id){
        employeeService.deleteById(id);
    }

}
