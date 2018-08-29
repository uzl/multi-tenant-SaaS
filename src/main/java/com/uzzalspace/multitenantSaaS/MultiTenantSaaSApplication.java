package com.uzzalspace.multitenantSaaS;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class MultiTenantSaaSApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiTenantSaaSApplication.class, args);
	}
}
