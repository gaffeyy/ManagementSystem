package com.wenku.documents_wenku.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenku.documents_wenku.common.BaseResponse;
import com.wenku.documents_wenku.common.BusinessErrors;
import com.wenku.documents_wenku.common.ResultUtils;
import com.wenku.documents_wenku.exception.BusinessException;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.model.request.DocumentDeleteBody;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.print.Doc;
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
	 * @return 文档
	 */
	@PostMapping("/upload")
	public BaseResponse<String> uploadDocument(HttpServletRequest request, @RequestParam("uploadDocument") MultipartFile uploadDocument){
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return ResultUtils.error(BusinessErrors.NOT_LOGIN);
		}
		if(uploadDocument == null){
			//请求参数错误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		documentService.documentUpload(uploadDocument);
		return ResultUtils.success(null,"上传成功");
	}

	/**
	 * 添加文档接口
	 * @param request
	 * @param uploadDocument
	 * @param documentName
	 * @param category
	 * @param documentTags
	 * @return 文档名称
	 */
	@PostMapping("/add")
	public BaseResponse<String> addDocument(HttpServletRequest request,@RequestParam("uploadDocument") MultipartFile uploadDocument,@RequestParam("documentName") String documentName,@RequestParam("category") String category,
							  @RequestParam("tags") String documentTags){
		if(StringUtils.isAnyBlank(documentTags,documentName,category)){
			//请求参数有误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return ResultUtils.error(BusinessErrors.NOT_LOGIN);
		}
		String modifiedURL = documentService.documentUpload(uploadDocument);
		String dName = documentName;
		String dTags = documentTags;
		String dCategory =category;
		String documentUrl = modifiedURL;
		String addedDocument = documentService.addDocument(dName, dCategory, currentUser.getId(), documentUrl, dTags);
		if(addedDocument == null){
			//添加失败
			return ResultUtils.error(BusinessErrors.SYSTEM_ERROR);
		}else {
			return ResultUtils.success(addedDocument,"添加成功");
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
	public BaseResponse<Long> deleteDocument(HttpServletRequest request, @RequestBody DocumentDeleteBody documentDeleteBody){
		if(documentDeleteBody == null){
			//请求参数有误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		boolean isAdmin = userService.isAdmin(request);
		User currentUser = userService.getCurrentUser(request);
		if(!isAdmin){
			//没有管理员权限
			return ResultUtils.error(BusinessErrors.NOT_ADMIN);
		}
		long deleteId = documentDeleteBody.getDeleteDocumentId();
		Long deleteResult = documentService.deleteDocument(currentUser.getId(), deleteId);
		if(deleteResult == null){
			//失败
			throw new BusinessException(BusinessErrors.SYSTEM_ERROR);
		}else {
			//成功
			return ResultUtils.success(deleteResult,"删除成功");
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
	public BaseResponse<Page<Document>> searchDocumentByName(HttpServletRequest request, @RequestParam("documentName") String documentName,@RequestParam("pageNum") long pageNum,
											   @RequestParam("pageSize") long pageSize){
		if(documentName == null || pageNum < 1L || pageSize < 1L){
			//请求参数有误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		String serachName = documentName;
		long searchPageNum = pageNum;
		long searchPageSize = pageSize;
		Page<Document> documentPage = documentService.searchDocumentByName(serachName, searchPageNum, searchPageSize);
		return ResultUtils.success(documentPage,"查询成功");
	}

	/**
	 * 根据文档ID查询
	 *
	 * @param request
	 * @param documentId
	 * @return 文档
	 */
	@PostMapping("/searchById")
	public BaseResponse<Document> searchDocumentById(HttpServletRequest request,@RequestParam("documentId") Long documentId){
		if(documentId == null){
			//请求参数有误
			throw new BusinessException(BusinessErrors.SYSTEM_ERROR);
		}
		long searchId = documentId;
		Document searchedDocumentById = documentService.searchDocumentById(searchId);
		if(searchedDocumentById == null){
			//未查询到
			return ResultUtils.error(BusinessErrors.NULL_ERROR);
		}else {
			//查询成功
			return ResultUtils.success(searchedDocumentById,"查询成功");
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
	public BaseResponse<Page<Document>> searchDocumentByTags(HttpServletRequest request,@RequestParam("documentTags") String tags,@RequestParam("pageNum") long pageNum,
											   @RequestParam("pageSize") long pageSize){
		if(tags == null || pageNum < 1L || pageSize < 1L){
			//请求参数错误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		String searchTags = tags;
		Page<Document> documentPage = documentService.searchDocumentByTags(searchTags, pageNum, pageSize);
		return ResultUtils.success(documentPage,"查询成功");
	}

	/**
	 * 文档推荐接口
	 *
	 * @param request
	 * @return 推荐文档
	 */
	@GetMapping("/recommend")
	public BaseResponse<List<Document>> recommendDocuemnts(HttpServletRequest request){
//		List<Document> documents = documentService.recommednDocument();
		List<Document> documents1 = documentService.redommendFromRedis();
		return ResultUtils.success(documents1,"查询成功");
	}

//	/**
//	 * 测试接口
//	 * @param request
//	 * @return
//	 */
//	@GetMapping("/recomendtest")
//	public BaseResponse<List<Document>> recommendTest(HttpServletRequest request){
//				List<Document> documents = documentService.recommednDocument();
////		List<Document> documents1 = documentService.redommendFromRedis();
//		return ResultUtils.success(documents,"查询成功");
//	}

}
