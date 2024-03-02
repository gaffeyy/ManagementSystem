package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.mapper.DocumentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author gaffey
* @description 针对表【document】的数据库操作Service实现
* @createDate 2024-03-02 17:42:31
*/
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>
    implements DocumentService {

	@Override
	public String addDocument(String documentName, String category, long uploadUser, String documentUrl, String tasg) {
		return null;
	}

	@Override
	public Long deleteDocument(long userId, long documentId) {
		return null;
	}

	@Override
	public List<Document> searchDocumentByName(String documentName) {
		return null;
	}

	@Override
	public Document searchDocumentById(long documentId) {
		return null;
	}

	@Override
	public List<Document> searchDocumentByTags(String tags) {
		return null;
	}
}




