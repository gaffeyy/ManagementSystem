package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.service.UserService;
import com.wenku.documents_wenku.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author gaffey
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-02 17:45:49
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

	private static final String SALT = "gaffeyUser";  // 盐值,混淆密码
	@Resource UserMapper userMapper;


	@Override
	public long userRegesiter(String userAccount, String userPassword, String checkPassword) {
		if(userAccount == null || userPassword == null || checkPassword == null){
			//输入参数有误
			return -1;
		}
		if(userPassword != checkPassword){
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
	public User userLogin(String userAccount, String userPassword) {
		if(userAccount == null || userPassword == null){
			//输入参数有误
			return null;
		}
		//密码加密
		String encrptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount",userAccount);
		queryWrapper.eq("userPassword",encrptPassword);
		User loginUser = userMapper.selectOne(queryWrapper);
		if(loginUser == null){
			//未查找到用户
			return null;
		}else {
			//登录成功
			return loginUser;
		}
	}

	@Override
	public int userLogout(HttpServletRequest request) {
		return 0;
	}

	@Override
	public User getCurrentUser(HttpServletRequest request) {
		return null;
	}

	@Override
	public boolean isAdmin(HttpServletRequest request) {
		return false;
	}

	@Override
	public String setLike(long documentId) {
		return null;
	}

	@Override
	public String unSetLike(long documentId) {
		return null;
	}
}




