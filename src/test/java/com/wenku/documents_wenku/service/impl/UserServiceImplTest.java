package com.wenku.documents_wenku.service.impl;

import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceImplTest {

	@Resource
	UserMapper userMapper;
	@Test
	public void testMysql(){
		User user = new User();
		user.setUserAccount("123");
		user.setUserPassword("123");
		userMapper.insert(user);
	}
}