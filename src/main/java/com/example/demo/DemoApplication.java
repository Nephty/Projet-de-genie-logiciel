package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.repository.*;
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

	CommandLineRunner run(AccountAccessRepo accountAccessRepo, BankRepo bankRepo, CurrencyTypeRepo currencyTypeRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			System.out.println("Runner be RUNNIIIIIIIING");
			CurrencyType currencyType = currencyTypeRepo.getById(0);
			bankRepo.save(new Bank(
					"ABCD",
					"Belfius",
					"uwu",
					passwordEncoder().encode("ABCD"),
					"uwuwuwuwu",
					"BE",
					currencyType
			));
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
