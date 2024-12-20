package com.codingshuttle.TestingApp;

import com.codingshuttle.TestingApp.services.impl.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class TestingAppApplication implements CommandLineRunner {

	private final DataService dataService;

	public static void main(String[] args) {

		SpringApplication.run(TestingAppApplication.class, args);
		System.out.println("welcome to the springboot application");
	}


	@Override
	public void run(String... args) throws Exception {
		dataService.getData();
	}
}
