package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.constant.Constant;
import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.service.UserService;
import com.wenku.documents_wenku.mapper.UserMapper;
import com.wenku.documents_wenku.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author gaffey
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-02 17:45:49
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

	private static final String SALT = "gaffeyUser";  // 盐值,混淆密码
	@Resource
	private UserMapper userMapper;

	@Resource
	private CookieUtils cookieUtils;

	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	@Override
	public long userRegesiter(String userAccount, String userPassword, String checkPassword) {
		if(userAccount == null || userPassword == null || checkPassword == null){
			//输入参数有误
			return -1;
		}
		if(!userPassword.equals(checkPassword)){
			//输入参数有误
			return -1;
		}
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount",userAccount);
		User selectUser = userMapper.selectOne(queryWrapper);
		if(selectUser != null){
			//账号已存在，不允许重复
			return -1;
		}
		User newUser = new User();
		newUser.setUserAccount(userAccount);
		//对密码进行加密
		String encrptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
		newUser.setUserPassword(encrptPassword);
		boolean saved = this.save(newUser);
		if(saved){
			//添加成功
			return newUser.getId();
		}else {
			//添加失败
			return -1;
		}
	}

	@Override
	public User userLogin(HttpServletRequest request, HttpServletResponse response, String userAccount, String userPassword) {
//		if(userAccount == null || userPassword == null){
//			//输入参数有误
//			return null;
//		}
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		Cookie cookie = cookieUtils.getCookie(request, Constant.USER_LOGIN_STATE);
//		if(cookie == null){
//			//请求参数有误
//			return null;
//		}
		if(cookie != null &&redisTemplate.hasKey(cookie.getValue())){
			//已登录
			if(userAccount == null && userPassword == null){
				queryWrapper.eq("userAccount",redisTemplate.opsForValue().get(cookie.getValue()));
				User loginedUser = userMapper.selectOne(queryWrapper);
				System.out.println("已登录");
				return loginedUser;
			}else if(StringUtils.isAnyBlank(userAccount,userPassword)){
				//请求参数有误
				return null;
			}else {
				//密码加密
				String encrptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
				queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("userAccount",userAccount);
				queryWrapper.eq("userPassword",encrptPassword);
				User loginedUser = userMapper.selectOne(queryWrapper);
				String uuid = UUID.randomUUID().toString();

				//当前登录账户与所带Cookie登录账户信息不一致，移除其redis登录态
				if(redisTemplate.delete(cookie.getValue())){
					//移除Cookie
					cookieUtils.removeCookie(request,response,Constant.USER_LOGIN_STATE);
				}
				//添加cookie
				cookieUtils.addCookie(request,response, Constant.USER_LOGIN_STATE, uuid, 30 * 60);
				redisTemplate.opsForValue().set(uuid,loginedUser.getUserAccount());
				redisTemplate.expire(uuid,30, TimeUnit.MINUTES);
				//登录成功
				return loginedUser;
			}

		}else {
			//未登录,执行登录
			//密码加密
			String encrptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
			queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("userAccount",userAccount);
			queryWrapper.eq("userPassword",encrptPassword);
			User loginUser = userMapper.selectOne(queryWrapper);
			String uuid = UUID.randomUUID().toString();
			//添加cookie
			cookieUtils.addCookie(request,response, Constant.USER_LOGIN_STATE, uuid, 30 * 60);
			redisTemplate.opsForValue().set(uuid,loginUser.getUserAccount());
			redisTemplate.expire(uuid,30, TimeUnit.MINUTES);

			//登录成功
			return loginUser;
		}
	}

	@Override
	public int userLogout(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = cookieUtils.getCookie(request, Constant.USER_LOGIN_STATE);
		if(!redisTemplate.hasKey(cookie.getValue())){
			//未登录
			return 0;
		}
		if(redisTemplate.delete(cookie.getValue())){
			//移除Cookie
			cookieUtils.removeCookie(request,response,Constant.USER_LOGIN_STATE);
			return 1;
		}
		//系统异常
		return 0;
	}

	@Override
	public User getCurrentUser(HttpServletRequest request) {
		String uuid = cookieUtils.getCookieValue(request, Constant.USER_LOGIN_STATE);
		if(uuid == null){
			//未登录
			return null;
		}
		Object userAccount = redisTemplate.opsForValue().get(uuid);
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount",(String) userAccount);
		User currentUser = userMapper.selectOne(queryWrapper);
		if(currentUser == null){
			//不存在该用户
			return null;
		}
		return currentUser;
	}

	@Override
	public boolean isAdmin(User user) {
		return user.getUserRole() == Constant.ADMIN_USER ? true : false;
	}

	@Override
	public boolean isAdmin(HttpServletRequest request) {
		User currentUser = getCurrentUser(request);
		if(currentUser == null){
			//不存在该用户
			return false;
		}
		return currentUser.getUserRole() == Constant.ADMIN_USER ? true : false;
	}

	@Override
	public Long setLike(long documentId, long userId) {
		String Key = RedisConstant.USER_LIKE_SET_INREDISKEY + Long.toString(documentId);
		String HashKey = RedisConstant.DOCUMENT_COUNT_REDIS + Long.toString(documentId);
		Long addResult = redisTemplate.opsForSet().add(Key, userId);
		if(addResult == 0){
			//用户已经点过赞,取消点赞
			redisTemplate.opsForSet().remove(Key,userId);
			Long likes = redisTemplate.opsForHash().increment(HashKey, RedisConstant.HASH_LIKECOUNT, -1);
			return likes;
		}
		Long likes = redisTemplate.opsForHash().increment(HashKey, RedisConstant.HASH_LIKECOUNT, 1);
		return likes;
	}

	@Override
	public Long setBrowser(Long documentId, long userId) {
		String Key = RedisConstant.DOCUMENT_COUNT_REDIS+documentId;
		redisTemplate.opsForHash().increment(Key,RedisConstant.HASH_READCOUNT,1);
		return documentId;
	}

}




