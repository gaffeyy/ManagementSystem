package com.wenku.documents_wenku.service;

import com.wenku.documents_wenku.model.domain.Document;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author gaffey
* @description 针对表【document】的数据库操作Service
* @createDate 2024-03-02 17:42:31
*/
public interface DocumentService extends IService<Document> {

	/**
	 *
	 * 增加文档记录到数据库
	 *
	 * @param documentName
	 * @param category
	 * @param uploadUser
	 * @param documentUrl
	 * @param tasg
	 * @return 文档名称
	 */
	public String addDocument(String documentName,String category,long uploadUser,
							  String documentUrl,String tasg);

	/**
	 *
	 * 删除指定ID的文档
	 *
	 * @param userId
	 * @param documentId
	 * @return 被删除文档的ID
	 */
	public Long deleteDocument(long userId,long documentId);

	/**
	 *
	 * 根据文档名搜索
	 *
	 * @param documentName
	 * @return 文档信息
	 */
	public List<Document> searchDocumentByName(String documentName);

	/**
	 *
	 * 根据Id搜索文档
	 *
	 * @param documentId
	 * @return 文档信息
	 */
	public Document searchDocumentById(long documentId);

	/**
	 *
	 * 根据标签搜索文档
	 *
	 * @param tags
	 * @return 文档信息
	 */
	public List<Document> searchDocumentByTags(String tags);

	// TODO 推荐收藏最高的十个文档
}
