package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.mapper.DocumentMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
* @author gaffey
* @description 针对表【document】的数据库操作Service实现
* @createDate 2024-03-02 17:42:31
*/
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>
    implements DocumentService {

	@Resource
	private DocumentMapper documentMapper;

	@Override
	public String addDocument(String documentName, String category, long uploadUser, String documentUrl, String tags) {
		if(StringUtils.isAnyBlank(documentName,category,documentUrl,tags)){
			//请求参数有误
			return null;
		}
		Document addDocument = new Document();
		addDocument.setDocumentName(documentName);
		addDocument.setCategory(category);
		addDocument.setUploadUserId(uploadUser);
		addDocument.setTags(tags);
		addDocument.setDucomentUrl(documentUrl);
		boolean savedState = this.save(addDocument);
		if(savedState){
			//添加成功
			return documentName + ":" +documentUrl;
		}else {
			//添加失败
			return null;
		}
	}

	@Override
	public Long deleteDocument(long userId, long documentId) {
		int i = documentMapper.deleteById(documentId);
		return documentId;
	}

	@Override
	public List<Document> searchDocumentByName(String documentName) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("documentName",documentName);
		List<Document> searchedDocuments = documentMapper.selectList(queryWrapper);
		if(searchedDocuments == null){
			//未查询到相关文档
			return null;
		}else {
			return searchedDocuments;
		}
	}

	@Override
	public Document searchDocumentById(long documentId) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("documentId",documentId);
		Document selectedDocument = documentMapper.selectOne(queryWrapper);
		if(selectedDocument == null){
			//未查询到ID为documentId的文档
			return null;
		}else {
			//查询成功返回
			return selectedDocument;
		}

	}

	@Override
	public List<Document> searchDocumentByTags(String tags) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("tags",tags);
		List<Document> selectedDocuments = documentMapper.selectList(queryWrapper);
		return selectedDocuments;
	}

	@Override
	public String documentUpload(MultipartFile uploadDocument) {
		String fileOriginalName = uploadDocument.getOriginalFilename();
		System.out.println(fileOriginalName);
//		String  fileSavedPath = "D:\\Intellji IDEA\\Database_ManagementSystem\\ManagementSystem\\SavedFile\\";
//		String DBfileSavedPath = "http://localhost:8081/api/upload/";
//		java.io.File file = new java.io.File(fileSavedPath+fileOriginalName);
//		try{
//			uploadDocument.transferTo(file);
//			System.out.println("上传成功");
//			return DBfileSavedPath+fileOriginalName;
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		return "success";
	}
}




