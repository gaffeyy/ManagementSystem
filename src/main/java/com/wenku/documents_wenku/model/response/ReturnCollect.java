package com.wenku.documents_wenku.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * 返回用户收藏
 *
 */
@Data
public class ReturnCollect implements Serializable {
		private Long documentId;
		private String documentUrl;
}
