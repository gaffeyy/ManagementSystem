package com.wenku.documents_wenku.mapper;

import com.wenku.documents_wenku.model.domain.Document;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author gaffey
* @description 针对表【document】的数据库操作Mapper
* @createDate 2024-03-02 18:29:01
* @Entity generator.domain.Document
*/
public interface DocumentMapper extends BaseMapper<Document> {
	@Select("select * from document order by likes desc limit 10")
	public List<Document> selectTopTenDocument();
	
	@Update("update document set likes = #{likes}, browser = #{browser} where documentId = #{documentId}")
	public boolean updateLikesAndBrowser(@Param("likes") Long likes,@Param("browser") Long browser,@Param("documentId") Long documentId);
}




