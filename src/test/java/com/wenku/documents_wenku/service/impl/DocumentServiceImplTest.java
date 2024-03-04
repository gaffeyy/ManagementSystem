package com.wenku.documents_wenku.service.impl;

import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
		List<Document> documents = documentService.searchDocumentByName("计算机");
		System.out.println(documents);
	}

	@Test
	void searchDocumentById() {
		Document document = documentService.searchDocumentById(1);
		System.out.println(document);
	}

	@Test
	void searchDocumentByTags() {
		List<Document> documents = documentService.searchDocumentByTags("教");
		System.out.println(documents);
	}
}