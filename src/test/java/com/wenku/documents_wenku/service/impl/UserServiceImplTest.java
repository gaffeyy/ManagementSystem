package com.wenku.documents_wenku.service.impl;
import java.util.ArrayList;
import java.util.Date;

import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.mapper.UserMapper;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.service.UserService;
import com.wenku.documents_wenku.utils.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tomcat.jni.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
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
	DocumentService documentService;

	@Resource
	RedisTemplate<String,Object> redisTemplate1;
	@Resource
	UserService userService;

	@Resource
	RedissonClient redissonClient;
	@Test
	public void testMysql(){
//		User user = new User();
//		user.setUserAccount("123");
//		user.setUserPassword("123");
//		userMapper.insert(user);
		System.out.println(new Date());
	}
	@Test
	public void testRedis(){
		List<Document> documents = documentService.recommednDocument();

//		redisTemplate.opsForList().set(RedisConstant.RECOMEND_TOP_DOCUMENT,0,document);
		int size = documents.size();
//		System.out.println(size);
//		for(int i = 0;i < size;i++){
//			redisTemplate.opsForList().leftPush(RedisConstant.RECOMEND_TOP_DOCUMENT,documents.get(i));
//		}
		Long size1 = redisTemplate.opsForList().size(RedisConstant.RECOMEND_TOP_DOCUMENT);
		List<Document> range = redisTemplate.opsForList().range(RedisConstant.RECOMEND_TOP_DOCUMENT, 0, size1);
		System.out.println(range);
		RList<Document> list = redissonClient.getList(RedisConstant.RECOMEND_TOP_DOCUMENT);
		System.out.println(list);
//		redisTemplate.opsForList().leftPushAll(documents);

	}
	@Test
	public void removeReidi(){
		Long size = redisTemplate.opsForList().size(RedisConstant.RECOMEND_TOP_DOCUMENT);
		System.out.println(size);
		for(int i =0;i <size;i++){
			redisTemplate.opsForList().leftPop(RedisConstant.RECOMEND_TOP_DOCUMENT);

		}
		Long size1= redisTemplate.opsForList().size(RedisConstant.RECOMEND_TOP_DOCUMENT);
		System.out.println(size1);

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
		String userAccount = "12345";
		String userPassword = "12345";
		String checkPassword = "12345";
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

	@Test
	void setLike() {
		long docuemntId = 1;
		long userId = 2;
		Object l = userService.setLike(docuemntId, userId);
		System.out.println(l);
	}

	@Test
	void unSetLike() {
	}

	@Test
	void persistLikeandReadCount(){
		Set keys = redisTemplate.keys(RedisConstant.DOCUMENT_COUNT_REDIS + "*");
		List<String > list = new ArrayList<>(keys);
		System.out.println(list);
		List<Long> documentId = new ArrayList<>();
		for(String str : list){
			documentId.add(Long.valueOf(str.substring(RedisConstant.DOCUMENT_COUNT_REDIS.length(),str.length())));
		}
		System.out.println(documentId);
		for (Long id : documentId){
			Document document = documentService.searchDocumentById(id);
			if(document != null){
				System.out.println(document);
				long likes = document.getLikes();
				long browser = document.getBrowser();
				System.out.println("Before   浏览："+browser +"点赞"+ likes);
				Object readcount = redisTemplate.opsForHash().get(RedisConstant.DOCUMENT_COUNT_REDIS + id, "readcount");
				Object likecount = redisTemplate.opsForHash().get(RedisConstant.DOCUMENT_COUNT_REDIS + id, "likecount");
				if(readcount != null){
					browser += (Integer) readcount;
				}
				if(likecount != null){
					likes += (Integer)(likecount);
//					redisTemplate.opsForHash().put(RedisConstant.DOCUMENT_COUNT_REDIS + id, "likecount",null);
				}
				boolean update = documentService.updateLandB(likes, browser, id);
				if(update && redisTemplate.hasKey(RedisConstant.DOCUMENT_COUNT_REDIS + id)){
					//更新成功
					redisTemplate.delete(RedisConstant.DOCUMENT_COUNT_REDIS + id);
				}else {
					//更新失败
					System.out.println("Error");
				}
				System.out.println("After   浏览："+browser +"点赞"+ likes);
//			System.out.println(document);
			}

		}

	}
	@Test
	public void persisitUserLike(){
		Long size = redisTemplate.opsForSet().size(RedisConstant.USER_LIKE_SET_INREDISKEY);
		System.out.println(size);
		Set members = redisTemplate.opsForSet().members(RedisConstant.USER_LIKE_SET_INREDISKEY);

		List<String> list = new ArrayList<>(members);
		System.out.println(list);
	}
}