package com.wenku.documents_wenku;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.wenku.documents_wenku.mapper")
@SpringBootApplication
public class DocumentsWenKuApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentsWenKuApplication.class, args);
	}

}
