package com.wenku.documents_wenku.service;

import com.wenku.documents_wenku.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author gaffey
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-02 17:45:49
*/
public interface UserService extends IService<User> {

	/**
	 *
	 * 用户注册
	 *
	 * @param userAccount
	 * @param userPassword
	 * @param checkPassword
	 * @return 用户id(主键)
	 */
	public long userRegesiter(String userAccount,String userPassword,String checkPassword);

	/**
	 * 用户登录
	 *
	 * @param request
	 * @param response
	 * @param userAccount
	 * @param userPassword
	 * @return 用户脱敏后信息
	 */
	public User userLogin(HttpServletRequest request, HttpServletResponse response,String userAccount, String userPassword);

	/**
	 * 用户注销
	 *
	 * @param request
	 * @param response
	 * @return 1 - 成功; 0 - 失败
	 */
	public int userLogout(HttpServletRequest request,HttpServletResponse response);

	/**
	 *
	 * 获取当前登录用户信息
	 *
	 * @param request
	 * @return 当前登录用户信息
	 */
	public User getCurrentUser(HttpServletRequest request);

	/**
     * 当前用户是否是管理员
     *
     * @return 1 - 是; 0 - 否
     */
	public boolean isAdmin(User user);

	/**
	 *
	 * 当前用户是否是管理员
	 *
	 * @param request
	 * @return 1 - 是; 0 - 否
	 */
	public boolean isAdmin(HttpServletRequest request);

	/**
	 *
	 * 给文档点赞
	 *
	 * @param documentId
	 * @return 文档名称
	 */
	public String setLike(long documentId);

	/**
	 *
	 * 给文档取消点赞
	 *
	 * @param documentId
	 * @return
	 */
	public String unSetLike(long documentId);
}
