package com.SIEBS.ITCompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ItCompanyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItCompanyApplication.class, args);
	}


}
