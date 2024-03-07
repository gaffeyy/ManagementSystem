package com.wenku.documents_wenku.service.impl;

import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.mapper.UserMapper;
import com.wenku.documents_wenku.service.UserService;
import com.wenku.documents_wenku.utils.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tomcat.jni.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
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
		Set keys = redisTemplate.keys("*");
		List<String> list = keys.stream().toList();
		System.out.println(list.toString());
		String hkey = "document:count:1";
		String readcount = "readcount";
		String likescount = "likescount";
//		redisTemplate.opsForHash().increment(hkey,readcount,1);
		Object rc = redisTemplate.opsForHash().get("document:count:1", "readcount");
		System.out.println(rc);
		System.out.println("=================");
		redisTemplate.opsForSet().add("document:collect:documentId","userId1");
		Boolean userId1 = redisTemplate.opsForSet().isMember("document:collect:documentId", "userId2");
		System.out.println(userId1);
//		redisTemplate.opsForHash().put("document:count:1","readcount",1);
//		redisTemplate.opsForHash().put("document:count:1","likescount",1);
		Object readcount1 = redisTemplate.opsForHash().get("document:count:1", "readcount");
		String s = readcount1.toString();
		System.out.println(readcount1);
//		System.out.println(readcount1.getClass());
		redisTemplate.opsForHash().increment("document:count:1","readcount",1);

		redisTemplate.opsForList().leftPushAll("user","1","2","2");
		List user = redisTemplate.opsForList().range("user", 0, 5);
		System.out.println(user);
//		readcount1 = redisTemplate.opsForHash().get("document:count:1", "readcount");
//		System.out.println(readcount1);
//		System.out.println(list.get(2));
//		System.out.println(value);
//		Assertions.assertEquals(value,"gaffey");
	}

	@Resource
	private FtpUtils ftpUtil;
	@Test
	public void testFtp(){
		FTPClient ftpClient = ftpUtil.getFtpClient();
		System.out.println(ftpClient.getRemoteAddress());
		String myfile = "file";
		File file = new File();

//		ftpUtil.uploadFileToFtp("/Document",myfile,file);
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

	@Test
	void userLogin() {
		User user = new User();
		user.setUserAccount("12345");
		user.setUserPassword("12345");
//		userService.userLogin();
	}
}