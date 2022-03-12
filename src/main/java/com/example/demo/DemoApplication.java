package com.example.demo;

import com.example.demo.model.CurrencyType;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.http.HttpResponse;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}


	@Bean
	CommandLineRunner run(AccountRepo accountRepo) {
		return args -> {
			System.out.println("Runner be RUNNIIIIIIIING");
			accountRepo.getAccountByUserIdAndSwift("02103107707","ABCD").ifPresentOrElse(
					System.out::println,
					() -> System.out.println("User with this ID not found")
			);
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
