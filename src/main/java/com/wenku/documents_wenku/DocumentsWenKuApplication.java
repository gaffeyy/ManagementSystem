package com.wenku.documents_wenku;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.wenku.documents_wenku.mapper")
@SpringBootApplication
@EnableScheduling
public class DocumentsWenKuApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentsWenKuApplication.class, args);
	}

}
