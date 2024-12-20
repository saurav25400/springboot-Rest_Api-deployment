package com.codingshuttle.TestingApp.controllers;

import com.codingshuttle.TestingApp.TestContainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;


//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
//@AutoConfigureWebTestClient
//@Import(TestContainerConfiguration.class)

class EmployeeControllerTest_Integration_Test extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private  EmployeeRepository employeeRepository;

    private Employee testEmployee;

    private EmployeeDto testEmployeeDto;



    @BeforeEach
    void setUpEmployee(){
        testEmployee= Employee.builder()
                .id(1L)
                .name("saurav")
                .email("saurav25400@gmail.com")
                .salary(100L)
                .build();


        testEmployeeDto=EmployeeDto.builder()
                .id(1L)
                .name("saurav")
                .email("saurav25400@gmail.com")
                .salary(100L)
                .build();
        employeeRepository.deleteAll();
    }





    @Test
    void testGetEmployeeById_success(){
        //arrange
        Employee savedEmployee=employeeRepository.save(testEmployee);

        //use webTestClient
        webTestClient.get().uri("/employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName())
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail());


//                .value(matcher->{
//                    Assertions.assertThat(matcher.getEmail()).isEqualTo(testEmployeeDto.getEmail());
//                    Assertions.assertThat(matcher.getName()).isEqualTo(testEmployeeDto.getName());
//                });



    }

    @Test
    void testGetEmployeeById_Failure(){
        webTestClient.get().uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void testCreateEmployee_whenEmployeeDoesExist_throwsException(){
        Employee savedEmployee=employeeRepository.save(testEmployee);

        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testCreateEmployee_whenEmployeeDoesNotExist(){
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName());
    }
    @Test
    void testUpdateEmployee_whenEmployeeNOtExistThrowxception(){
        webTestClient.put()
                .uri("/employees/100")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    void testUpdateEmployee_whenAttemptingTOUpdateEmail_ThrowsException(){
        Employee savedEmployee=employeeRepository.save(testEmployee);
        testEmployeeDto.setName("random");
        testEmployeeDto.setEmail("random@gmail.com");
        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus()
                .is5xxServerError();


    }
    @Test
    void testUpdateEmployee_whenEmployeeExist(){
        Employee savedEmployee=employeeRepository.save(testEmployee);
        testEmployeeDto.setName("random");
        testEmployeeDto.setSalary(1000L);

        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testDeleteEmployee_whenEmployeeNotExist_ThrowException(){
        webTestClient.delete()
                .uri("/employees/100")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExist(){
        Employee savedEmployee=employeeRepository.save(testEmployee);

        webTestClient.delete()
                .uri("/employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

//        webTestClient.delete()
//                .uri("/employees/100")
//                .exchange()
//                .expectStatus().isNotFound();







    }

}

