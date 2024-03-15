package com.wenku.documents_wenku.mapper;

import com.wenku.documents_wenku.model.domain.Usercollect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author gaffey
* @description 针对表【userCollect(用户点赞表)】的数据库操作Mapper
* @createDate 2024-03-11 11:11:17
* @Entity generator.domain.Usercollect
*/
public interface UsercollectMapper extends BaseMapper<Usercollect> {
//	@Select("select userId,documentUrl from userCollect where")
	@Select("select documentName,documentUrl from userCollect where userId = #{userId}")
	public List<Usercollect> searchCollect(@Param("userId") Long userId);
}




