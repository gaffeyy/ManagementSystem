package com.wenku.documents_wenku.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.common.BusinessErrors;
import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.exception.BusinessException;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.mapper.DocumentMapper;
import com.wenku.documents_wenku.utils.FtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author gaffey
* @description 针对表【document】的数据库操作Service实现
* @createDate 2024-03-02 17:42:31
*/
@Service
@Slf4j
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>
    implements DocumentService {

	@Resource
	private DocumentMapper documentMapper;

	@Resource
	private RedissonClient redissionClient;

	@Override
	public String addDocument(String documentName, String category, long uploadUser, String documentUrl, String tags) {
		if(StringUtils.isAnyBlank(documentName,category,documentUrl,tags)){
			//请求参数有误
			log.error("请求参数有误"+ new Date());
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
			log.error("添加文档失败"+new Date());
			throw new BusinessException(BusinessErrors.SYSTEM_ERROR);
		}
	}

	@Override
	public Long deleteDocument(long userId, long documentId) {
		int i = documentMapper.deleteById(documentId);
		return documentId;
	}

	@Override
	public Page<Document> searchDocumentByName(String documentName, long pageNum, long pageSize) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("documentName",documentName);
		Page<Document> searchPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
//		List<Document> searchedDocuments = documentMapper.selectList(queryWrapper);
		if(searchPage == null){
			//未查询到相关文档
			return null;
		}else {
			return searchPage;
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
			return getSafetyDoc(selectedDocument);
		}

	}

	@Override
	public Page<Document> searchDocumentByTags(String tags, long pageNum, long pageSize) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("tags",tags);
		List<Document> selectedDocuments = documentMapper.selectList(queryWrapper);
		Page<Document> documentPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
		return documentPage;
	}

	@Override
	public String documentUpload(MultipartFile uploadDocument) {
		String fileOriginalName = uploadDocument.getOriginalFilename();
		System.out.println(fileOriginalName);
		FtpUtils ftpUtil = new FtpUtils();

		String newname=new String();

		if(uploadDocument!=null){
			//文件名称 = 时间戳 + 文件自己的名字；
			newname = System.currentTimeMillis()+uploadDocument.getOriginalFilename();

			try {
				boolean hopson = ftpUtil.uploadFileToFtp("Document", newname, uploadDocument.getInputStream());
				if(hopson) {  //上传成功
					log.info("文件上传服务器成功 ---- "+newname);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("文件上传服务器失败 ---- "+e);
				return null;
			}
		}
		return newname;
	}

	@Override
	public List<Document> recommednDocument() {
		List<Document> documents = documentMapper.selectTopTenDocument();
		return documents.stream().map(this::getSafetyDoc).collect(Collectors.toList());
	}

	@Override
	public List<Document> redommendFromRedis() {
		RList<Document> list = redissionClient.getList(RedisConstant.RECOMEND_TOP_DOCUMENT);
		return 	list.stream().map(this::getSafetyDoc).collect(Collectors.toList());
	}

	@Override
	public boolean updateLandB(Long likes, Long browser, Long documentId) {
		boolean updateResult = documentMapper.updateLikesAndBrowser(likes, browser, documentId);
		if(!updateResult){
			log.error("更新点赞和浏览量失败 ---- "+new Date());
		}
		return updateResult;
	}

	public Document getSafetyDoc(Document document){
		Document safetyDoc = new Document();
		safetyDoc.setDocumentId(document.getDocumentId());
		safetyDoc.setDocumentName(document.getDocumentName());
		safetyDoc.setCategory(document.getCategory());
//		safetyDoc.setUploadUserId(0L);
//		safetyDoc.setUploadTime(new Date());
//		safetyDoc.setIsDelete(0);
		safetyDoc.setDucomentUrl(document.getDucomentUrl());
		safetyDoc.setTags(document.getTags());
		safetyDoc.setLikes(document.getLikes());
		safetyDoc.setBrowser(document.getBrowser());
		return safetyDoc;
	}
}




