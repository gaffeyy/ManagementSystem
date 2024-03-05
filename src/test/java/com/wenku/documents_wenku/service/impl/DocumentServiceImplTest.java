package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

@SpringBootTest
class DocumentServiceImplTest {

	@Resource
	private DocumentService documentService;
	@Test
	void addDocument() {
		String documentName = "计算机网路";
		String category = "计算机";
		long uploadUser = 1;
		String documentUrl = "url";
		String tags = "教学";
		String s = documentService.addDocument(documentName, category, uploadUser, documentUrl, tags);
		System.out.println(s);

	}

	@Test
	void deleteDocument() {
		long userId = 1;
		long documentId = 2;
		Long l = documentService.deleteDocument(1, 2);
		System.out.println(l);
	}

	@Test
	void searchDocumentByName() {
		long start = System.currentTimeMillis();
		Page<Document> documentPage = documentService.searchDocumentByName("计算机", 22, 22);
//		System.out.println(documents);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	void searchDocumentById() {
		long start = System.currentTimeMillis();
		Document document = documentService.searchDocumentById(1000);
//		System.out.println(document);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	void searchDocumentByTags() {
		long start = System.currentTimeMillis();
		Page<Document> documentPage = documentService.searchDocumentByTags("教", 10, 10);
//		System.out.println(documents);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}