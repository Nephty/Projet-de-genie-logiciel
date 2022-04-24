package com.example.demo;

import com.example.demo.scheduler.TransactionScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;


@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}

	//@Bean
	CommandLineRunner run(TransactionScheduler transactionScheduler) {
		return args -> {
			System.out.println("Runner be RUNNIIIIIIIING");
			transactionScheduler.performDueTransactions();
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}

}
