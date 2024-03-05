package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.mapper.DocumentMapper;
import com.wenku.documents_wenku.utils.FtpUtils;
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
			return selectedDocument;
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

		//给起一个新的文件名称，防止名称重复
		String newname=new String();

		if(uploadDocument!=null){
			//文件名称 = 时间戳 + 文件自己的名字；
			newname = System.currentTimeMillis()+uploadDocument.getOriginalFilename();

			try {
				//图片上传，调用ftp工具类 image 上传的文件夹名称，newname 图片名称，inputStream
				boolean hopson = ftpUtil.uploadFileToFtp("Document", newname, uploadDocument.getInputStream());
				if(hopson) {  //上传成功
					// 文件信息入库
                   /* File file = new File();
                    file.setName(newname);
                    file.setSize(size);
                    file.setUrl(url);
                    file.setMassifId(massifId);
                    file.setPhoto(path + newname);
                    .....   等等业务处理
                    this.insert(dkPhoto);*/
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			}
		}
//		log.info("文件上传完成==============");
		return "success";
	}
}




