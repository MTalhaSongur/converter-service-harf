package com.converterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class ConverterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConverterServiceApplication.class, args);
	}
}
