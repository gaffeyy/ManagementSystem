package com.wenku.documents_wenku.controller;

import com.wenku.documents_wenku.constant.Constant;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.model.request.UserLoginBody;
import com.wenku.documents_wenku.model.request.UserRegisterBody;
import com.wenku.documents_wenku.service.UserService;
import com.wenku.documents_wenku.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * 用户接口
 *
 * @author gaffey
 * @createTime 2024/3/2 18:35
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Resource
	private UserService userService;

	@Resource
	private CookieUtils cookieUtils;

	/**
	 * 用户注册接口
	 *
	 * @param userRegisterBody
	 * @return 注册结果
	 */
	@PostMapping("/register")
	public long userRegister(@RequestBody UserRegisterBody userRegisterBody){
		if (userRegisterBody == null){
			//请求参数有误
			return -1;
		}
		String userAccount = userRegisterBody.getUserAccount();
		String userPassword = userRegisterBody.getUserPassword();
		String checkPassword = userRegisterBody.getCheckPassword();
		if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
			//请求参数有误
			return -1;
		}
		long registerResult = userService.userRegesiter(userAccount, userPassword, checkPassword);
		return registerResult;
	}

	/**
	 * 用户登录接口
	 *
	 * @param request
	 * @param response
	 * @param userLoginBody
	 * @return 用户信息
	 */
	@PostMapping("/login")
	public User userLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody UserLoginBody userLoginBody){
		if(userLoginBody == null && cookieUtils.getCookie(request, Constant.USER_LOGIN_STATE) == null){
			//请求参数有误
			return null;
		}
		log.info("login");
//		request.getRequestURL().
		String userAccount = userLoginBody.getUserAccount();
		String userPassword = userLoginBody.getUserPassword();
//		if(StringUtils.isAllBlank(userAccount,userPassword)){
//			//请求参数有误
//			return null;
//		}
		User loginUser = userService.userLogin(request, response, userAccount, userPassword);
		if(loginUser == null){
			//登录失败
			return null;
		}
		return loginUser;
	}

	/**
	 * 获取当前用户接口
	 *
	 * @param request
	 * @return 当前登录用户
	 */
	@GetMapping("/getCurrentUser")
	public User getCurrentUser(HttpServletRequest request){
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未获取到当前用户，未登录
			return null;
		}
		return currentUser;
	}

	/**
	 * 用户注销
	 * @param request
	 * @param response
	 * @return 1- 成功 0 - 失败
	 */
	@PostMapping("/logout")
	public int userLogout(HttpServletRequest request,HttpServletResponse response){
		int result = userService.userLogout(request, response);
		return result;
	}

	/**
	 * 用户点赞文档接口
	 *
	 * @param request
	 * @param documentId
	 * @return 文档的点赞数
	 */
	@PostMapping("/setLike")
	public Long userSetLike(HttpServletRequest request,Long documentId){
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return null;
		}
		Long lieksCount = userService.setLike(documentId, currentUser.getId());
		return lieksCount;
	}

	/**
	 * 为文章添加浏览记录
	 *
	 * @param request
	 * @param documentId
	 * @return
	 */
	@PostMapping("/setBrowser")
	public Long userBrowser(HttpServletRequest request,Long documentId){
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return null;
		}
		Long documentid = userService.setBrowser(documentId, currentUser.getId());
		return documentid;
	}
}
