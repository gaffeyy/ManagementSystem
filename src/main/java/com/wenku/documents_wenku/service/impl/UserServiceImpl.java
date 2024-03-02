package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.service.UserService;
import com.wenku.documents_wenku.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author gaffey
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-02 17:45:49
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

	@Override
	public long userRegesiter(String userAccount, String userPassword, String checkPassword) {
		return 0;
	}

	@Override
	public User userLogin(String userAccount, String userPassword) {
		return null;
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




