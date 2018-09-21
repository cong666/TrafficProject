package com.cc.traffic.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.cc"})
@MapperScan(basePackages = "com.cc.traffic.dao")
public class Application {

	public static void main(String[] args) {
		//run 
		SpringApplication.run(Application.class, args);
	}

}
