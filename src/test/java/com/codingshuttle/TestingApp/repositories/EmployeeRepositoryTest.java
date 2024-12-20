package com.codingshuttle.TestingApp.repositories;

import com.codingshuttle.TestingApp.TestContainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestContainerConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    private Employee employee;


    @BeforeEach
    void setUpEmployee(){
        employee=Employee.builder()
                .id(1L)
                .name("Saurav")
                .email("saurav25400@gmail.com")
                .salary(100L)
                .build();


    }

    @Test
    void findByEmail_whenEmailIsValid_returnEmployee(){
        // Arrange ->Given

        employeeRepository.save(employee);

        //Act on the methods -->When

        List<Employee>empList=employeeRepository.findByEmail(employee.getEmail());




        //Assertions-->Then

        Assertions.assertThat(empList).isNotEmpty();

       // Assertions.assertThat(empList).contains(employee);

    }

    @Test
    void findByEmail_WhenEmailInvalid_returnProperErrorMessage(){
        // given
        String errorMessage="notPresent.@gmail.com";


        // Then
        List<Employee>employeeList=employeeRepository.findByEmail(employee.getEmail());


        //Assertions
        Assertions.assertThat(employeeList)
                .isEmpty();
//        Assertions.assertThat(employeeList)
//                .isNotEmpty();

    }


}












