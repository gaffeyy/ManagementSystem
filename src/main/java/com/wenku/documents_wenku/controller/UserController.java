package com.wenku.documents_wenku.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenku.documents_wenku.common.BaseResponse;
import com.wenku.documents_wenku.common.BusinessErrors;
import com.wenku.documents_wenku.common.ResultUtils;
import com.wenku.documents_wenku.constant.Constant;
import com.wenku.documents_wenku.exception.BusinessException;
import com.wenku.documents_wenku.mapper.UsercollectMapper;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.model.domain.Usercollect;
import com.wenku.documents_wenku.model.request.UserCollectBody;
import com.wenku.documents_wenku.model.request.UserLoginBody;
import com.wenku.documents_wenku.model.request.UserRegisterBody;
import com.wenku.documents_wenku.model.response.ReturnCollect;
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
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterBody userRegisterBody){
		if (userRegisterBody == null){
			//请求参数有误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		String userAccount = userRegisterBody.getUserAccount();
		String userPassword = userRegisterBody.getUserPassword();
		String checkPassword = userRegisterBody.getCheckPassword();
		if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
			//请求参数有误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		Long registerResult = userService.userRegesiter(userAccount, userPassword, checkPassword);
		if(registerResult == null){
			return ResultUtils.error(BusinessErrors.SYSTEM_ERROR,"账号已存在");
		}
		return ResultUtils.success(registerResult,"注册成功");
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
	public BaseResponse<User> userLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody UserLoginBody userLoginBody){
		if(userLoginBody == null && cookieUtils.getCookie(request, Constant.USER_LOGIN_STATE) == null){
			//请求参数有误
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		String userAccount = userLoginBody.getUserAccount();
		String userPassword = userLoginBody.getUserPassword();
		User loginUser = userService.userLogin(request, response, userAccount, userPassword);
		if(loginUser == null){
			//登录失败
			return ResultUtils.error(BusinessErrors.NULL_ERROR);
		}
		return ResultUtils.success(loginUser,"登录成功");
	}

	/**
	 * 获取当前用户接口
	 *
	 * @param request
	 * @return 当前登录用户
	 */
	@GetMapping("/getCurrentUser")
	public BaseResponse<User> getCurrentUser(HttpServletRequest request){
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未获取到当前用户，未登录
			return ResultUtils.error(BusinessErrors.NOT_LOGIN);
		}
		return ResultUtils.success(currentUser,"");
	}

	/**
	 * 用户注销
	 * @param request
	 * @param response
	 * @return 1- 成功 0 - 失败
	 */
	@PostMapping("/logout")
	public BaseResponse<Integer> userLogout(HttpServletRequest request,HttpServletResponse response){
		int result = userService.userLogout(request, response);
		return ResultUtils.success(result,"注销成功");
	}

	/**
	 * 用户点赞文档接口
	 *
	 * @param request
	 * @param documentId
	 * @return 文档的点赞数
	 */
	@PostMapping("/setLike")
	public BaseResponse<Long> userSetLike(HttpServletRequest request,@RequestParam("documentId") Long documentId){
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return ResultUtils.error(BusinessErrors.NOT_LOGIN);
		}
		Long lieksCount = userService.setLike(documentId, currentUser.getId());
		return ResultUtils.success(lieksCount,"");
	}

	/**
	 * 为文章添加浏览记录
	 *
	 * @param request
	 * @param documentId
	 * @return
	 */
	@PostMapping("/setBrowser")
	public BaseResponse<Long> userBrowser(HttpServletRequest request,@RequestParam("documentId") Long documentId){
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return ResultUtils.error(BusinessErrors.NOT_LOGIN);
		}
		Long documentid = userService.setBrowser(documentId, currentUser.getId());
		return ResultUtils.success(documentid,"浏览记录加一");
	}

	/**
	 * 用户收藏文档接口
	 *
	 * @param request
	 * @param userCollectBody
	 * @return 文档Id
	 */
	@PostMapping("/collect")
	public BaseResponse<Long> userCollect(HttpServletRequest request, @RequestBody UserCollectBody userCollectBody){
		if(userCollectBody == null){
			//请求参数错误
			return ResultUtils.error(BusinessErrors.PARAMS_ERROR);
		}
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return ResultUtils.error(BusinessErrors.NOT_LOGIN);
		}
		Long l = userService.collectDoc(userCollectBody.getUserId(), userCollectBody.getDocumentId());
		if(l == null){
			throw new BusinessException(BusinessErrors.SYSTEM_ERROR);
		}
		return ResultUtils.success(l,"收藏成功");
	}

	@PostMapping("/getCollect")
	public BaseResponse<Page<Usercollect>> getCollect(HttpServletRequest request,@RequestParam("pageSize") Long pageSize,@RequestParam("pageNum") Long pageNum){
		if(pageNum==null || pageSize==null){
			throw new BusinessException(BusinessErrors.PARAMS_ERROR);
		}
		User currentUser = userService.getCurrentUser(request);
		if(currentUser == null){
			//未登录
			return ResultUtils.error(BusinessErrors.NOT_LOGIN,"用户未登录");
		}
		Page<Usercollect> usercollectPage = userService.getCollect(currentUser.getId(), pageNum, pageSize);
		return ResultUtils.success(usercollectPage,"查询成功");
//		return null;
	}
}
