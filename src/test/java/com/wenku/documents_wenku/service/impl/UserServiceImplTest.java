package com.wenku.documents_wenku.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.mapper.UserMapper;
import com.wenku.documents_wenku.service.UserService;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class UserServiceImplTest {

	@Resource
	UserMapper userMapper;

	@Resource
	RedisTemplate redisTemplate;

	@Resource
	RedisTemplate<String,Object> redisTemplate1;
	@Resource
	UserService userService;
	@Test
	public void testMysql(){
		User user = new User();
		user.setUserAccount("123");
		user.setUserPassword("123");
		userMapper.insert(user);
	}
	@Test
	public void testRedis(){
//		ObjectMapper objectMapper = new ObjectMapper();
//		ValueOperations ops = redisTemplate.opsForValue();
//		ops.set("uu22id","12234");
//		redisTemplate.expire("uuid",30, TimeUnit.MINUTES);
		// 设置键值对
		redisTemplate1.opsForValue().set("name", "kkkkk");
		redisTemplate1.expire("name",30,TimeUnit.SECONDS);

// 获取值
		String value = (String) redisTemplate.opsForValue().get("name");
		System.out.println(value);
//		Assertions.assertEquals(value,"gaffey");
	}

	@Test
	void userRegesiter() {
		String userAccount = "gaffet";
		String userPassword = "12345";
		String checkPassword = "";
		long l = userService.userRegesiter(userAccount, userPassword, checkPassword);
		System.out.println(l);
		Assertions.assertNotEquals(l,-1);
	}

}