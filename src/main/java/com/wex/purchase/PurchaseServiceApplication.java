package com.wex.purchase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@SpringBootApplication
public class PurchaseServiceApplication {

	public static void main(String[] args) {
		System.out.println(SpringVersion.getVersion());

		SpringApplication.run(PurchaseServiceApplication.class, args);
	}

}
