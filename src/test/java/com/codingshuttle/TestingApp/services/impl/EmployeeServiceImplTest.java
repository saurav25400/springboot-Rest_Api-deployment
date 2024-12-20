package com.codingshuttle.TestingApp.services.impl;

import com.codingshuttle.TestingApp.TestContainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.exceptions.ResourceNotFoundException;
import com.codingshuttle.TestingApp.repositories.EmployeeRepository;
import com.codingshuttle.TestingApp.services.EmployeeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

     @Spy // since this is the third party library that we are going to use here so it will be the original here.

     private ModelMapper modelMapper;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeServiceImp;

    private  Employee mockEmployee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setup(){
         mockEmployee=Employee.builder()
                .id(1L)
                .name("saurav")
                .email("saurav25400@gmail.com")
                .salary(25400L)
                .build();

         employeeDto=modelMapper.map(mockEmployee,EmployeeDto.class);


    }

    @Test
    void testGetEmployeeById_returnEmployee(){
        //Arrange-->given

        Long id=mockEmployee.getId();
        // mock the behaviour of objects
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));


        //act -->means calling the actual service method
        EmployeeDto employeeDto=employeeServiceImp.getEmployeeById(id);
        // assert

        Assertions.assertThat(employeeDto).isNotNull();
        Assertions.assertThat(employeeDto.getId()).isEqualTo(mockEmployee.getId());
        Assertions.assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());

        // verify
        verify(employeeRepository).findById(id);
    }

    @Test
    void testGetEmployeeById_throwException(){
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(()->employeeServiceImp.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        //verify
        verify(employeeRepository).findById(1L);


    }

    @Test
    void testCreateNewEmployee_whenEmployeeValid_createNewEmployee(){
        //arrange
        Long id=mockEmployee.getId();
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());

        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        // act

        EmployeeDto emp=employeeServiceImp.createNewEmployee(employeeDto);

        //assert
        Assertions.assertThat(emp).isNotNull();
        Assertions.assertThat(emp.getId()).isEqualTo(mockEmployee.getId());

        Assertions.assertThat(emp.getEmail()).isEqualTo(mockEmployee.getEmail());

        //  captures

        // verify
        verify(employeeRepository).save(any(Employee.class));
        ArgumentCaptor<Employee>employeeCapture=ArgumentCaptor.forClass(Employee.class);

        //verify that employee was captured while calling the saved method
        verify(employeeRepository).save(employeeCapture.capture());

        //capture

        Employee capturedEmployee=employeeCapture.getValue(); // the value that we will pass

        //assertions

        Assertions.assertThat(capturedEmployee.getId()).isEqualTo(id);
        Assertions.assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());


    }


    // findById-->sad test case





    //updateEmployees


    @Test
    void testCreateEmployee_whenEmployeeDoesNotExist_ThrowExceptions(){
        //arrange

        when(employeeRepository.findByEmail(employeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        //act and assert

        assertThatThrownBy(()->employeeServiceImp.createNewEmployee(employeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email:"+employeeDto.getEmail());

        //verify

        verify(employeeRepository).findByEmail(employeeDto.getEmail());

        verify(employeeRepository,never()).save(any());

    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExist_ThrowException(){
        //arrange
        Long id=mockEmployee.getId();
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(()->employeeServiceImp.updateEmployee(id,employeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        //verify
        verify(employeeRepository).findById(1L);
        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmail_whenAttemptingToUpdateEmail_ThrowsException(){
        //arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        mockEmployee.setName("random");
        mockEmployee.setEmail("random2@gmail.com");
        //act and assert
        assertThatThrownBy(()->employeeServiceImp.updateEmployee(1L,employeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        //verify
        verify(employeeRepository).findById(1L);
        verify(employeeRepository,never()).save(any());


    }

    @Test
    void testUpdateEmployee_whenEmployeeDtoIsValid(){
        when(employeeRepository.findById(employeeDto.getId())).thenReturn(Optional.of(mockEmployee));

        employeeDto.setName("random");

       employeeDto.setSalary(100L);

       Employee newEmployee=modelMapper.map(employeeDto, Employee.class);

       when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);


        //act
        EmployeeDto updatedEmployeeDto=employeeServiceImp.updateEmployee(1L,employeeDto);

        //assert
        Assertions.assertThat(employeeDto).isEqualTo(updatedEmployeeDto);


    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExist_throwsException(){
        when(employeeRepository.existsById(1L)).thenReturn(false);

        //act
        assertThatThrownBy(()->employeeServiceImp.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        //verify
        verify(employeeRepository,never()).deleteById(1L);


    }

    @Test
    void testdeleteEmployee_whenEmployeeExist(){
        when(employeeRepository.existsById(1L)).thenReturn(true);


//        act -->we can use verifyThatCode in case of  method returning void
        assertThatCode(()->employeeServiceImp.deleteEmployee(1L))
                .doesNotThrowAnyException();  // for methods returning void we can chek that methods does not throw any exceptions using
        //assertThatCode();

        //verify
        verify(employeeRepository).deleteById(1L);

    }







}