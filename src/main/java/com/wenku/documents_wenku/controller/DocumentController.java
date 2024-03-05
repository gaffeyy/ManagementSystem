package com.wenku.documents_wenku.controller;

import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.model.request.DocumentAddBody;
import com.wenku.documents_wenku.model.request.DocumentDeleteBody;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文档接口
 *
 * @author gaffey
 * @createTime 2024/3/2 18:36
 *
 */
@RestController
@RequestMapping("/document")
public class DocumentController {
	@Resource
	private DocumentService documentService;

	@Resource
	private UserService userService;

	/**
	 * 文件上传接口
	 *
	 * @param request
	 * @param uploadDocument
	 * @return 文件
	 */
	@PostMapping("/upload")
	public String uploadDocument(HttpServletRequest request,@RequestParam("uploadDocument") MultipartFile uploadDocument){
//		User currentUser = userService.getCurrentUser(request);
//		if(currentUser == null){
//			//未登录
//			return null;
//		}
		documentService.documentUpload(uploadDocument);
		return null;
	}

	/**
	 * 上传文档接口
	 * @param request
	 * @param uploadDocument
	 * @param documentName
	 * @param category
	 * @param documentTags
	 * @return 文档名称
	 */
	@PostMapping("/add")
	public String addDocument(HttpServletRequest request,@RequestParam("uploadDocument") MultipartFile uploadDocument,@RequestParam("documentName") String documentName,@RequestParam("category") String category,
							  @RequestParam("tags") String documentTags){
		if(StringUtils.isAnyBlank(documentTags,documentName,category)){
			//请求参数有误
			return null;
		}
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return null;
		}
		String modifiedURL = documentService.documentUpload(uploadDocument);
		String dName = documentName;
		String dTags = documentTags;
		String dCategory =category;
		String documentUrl = modifiedURL;
		String addedDocument = documentService.addDocument(documentName, category, currentUser.getId(), documentUrl, documentTags);
		if(addedDocument == null){
			//添加失败
			return null;
		}else {
			return addedDocument;
		}
	}

	/**
	 * 文档删除接口
	 *
	 * @param request
	 * @param documentDeleteBody
	 * @return 删除文档的ID
	 */
	@PostMapping("/delete")
	public Long deleteDocument(HttpServletRequest request, @RequestBody DocumentDeleteBody documentDeleteBody){
		if(documentDeleteBody == null){
			//请求参数有误
			return null;
		}
		boolean isAdmin = userService.isAdmin(request);
		User currentUser = userService.getCurrentUser(request);
		if(!isAdmin){
			//没有管理员权限
			return null;
		}
		long deleteId = documentDeleteBody.getDeleteDocumentId();
		Long deleteResult = documentService.deleteDocument(currentUser.getId(), deleteId);
		if(deleteResult == null){
			//失败
			return null;
		}else {
			//成功
			return deleteResult;
		}
	}

	/**
	 * 根据文档名称查询
	 *
	 * @param request
	 * @param documentName
	 * @return 文档
	 */
	@PostMapping("/searchByName")
	public List<Document> searchDocumentByName(HttpServletRequest request, @RequestParam("documentName") String documentName){
		if(documentName == null){
			//请求参数有误
			return null;
		}
		String serachName = documentName;
		List<Document> documentList = documentService.searchDocumentByName(documentName);
		return documentList;
	}

	/**
	 * 根据文档ID查询
	 *
	 * @param request
	 * @param documentId
	 * @return 文档
	 */
	@PostMapping("/searchById")
	public Document searchDocumentById(HttpServletRequest request,@RequestParam("documentId") Long documentId){
		if(documentId == null){
			//请求参数有误
			return null;
		}
		long searchId = documentId;
		Document searchedDocumentById = documentService.searchDocumentById(searchId);
		if(searchedDocumentById == null){
			//未查询到
			return null;
		}else {
			//查询成功
			return searchedDocumentById;
		}
	}

	/**
	 * 根据标签查询文档
	 *
	 * @param request
	 * @param tags
	 * @return 文档
	 */
	@PostMapping("/searchByTags")
	public List<Document> searchDocumentByTags(HttpServletRequest request,@RequestParam("documentTags") String tags){
		if(tags == null){
			//请求参数错误
			return null;
		}
		String searchTags = tags;
		List<Document> documentList = documentService.searchDocumentByTags(searchTags);
		return documentList;
	}

}
