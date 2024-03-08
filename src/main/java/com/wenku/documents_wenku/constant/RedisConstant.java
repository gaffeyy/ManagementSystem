package com.wenku.documents_wenku.constant;

public interface RedisConstant {
	/**
	 * Redis中用于记录用于点赞收藏的键（KEY）
	 */
	public static final String USER_LIKE_SET_INREDISKEY = "document:like:";
	public static final String USER_COLLECT_SET_INREDISKEY = "document:collect:";

	/**
	 * Redis中用于记录文档浏览量和点赞量的Hash KEY
	 */
	public static final String DOCUMENT_COUNT_REDIS = "document:count:";
	public static final String HASH_READCOUNT = "readcount";
	public static final String HASH_LIKECOUNT = "likecount";

	/**
	 * 每日推荐文档
	 */
	public static final String RECOMEND_TOP_DOCUMENT = "recommend:document";
}
