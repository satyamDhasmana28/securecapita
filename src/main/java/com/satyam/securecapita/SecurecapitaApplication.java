package com.satyam.securecapita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SecurecapitaApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext obj = SpringApplication.run(SecurecapitaApplication.class, args);
//		System.out.println("hey");
//		Arrays.stream(obj.getBeanDefinitionNames()).forEach(s -> System.out.println(s));
	}

}
