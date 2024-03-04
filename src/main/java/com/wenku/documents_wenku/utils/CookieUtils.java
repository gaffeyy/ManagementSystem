package com.wenku.documents_wenku.utils;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * Cookie工具类
 *
 * @author GaffEy
 */
@Component
public class CookieUtils {

	/**
	 * response添加cookie
	 *
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 */
	public void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge){
		Cookie[] cookies = request.getCookies();
		Cookie cookie = new Cookie(name,value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 获取Cookie
	 *
	 * @param request
	 * @param name
	 * @return
	 */
	public Cookie getCookie(HttpServletRequest request,String name){
		String cookieValue = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0){
			for (Cookie cookie : cookies){
				if(cookie.getName().equals(name)){
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * 获取Cookie的value
	 *
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public String getCookieValue(HttpServletRequest request,String cookieName){
		String cookieValue = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0){
			for (Cookie cookie : cookies){
				if(cookie.getName().equals(cookieName)){
					cookieValue = cookie.getValue();
				}
			}
		}
		return cookieValue;
	}

	/**
	 * 移除Cookie
	 *
	 * @param request
	 * @param response
	 * @param name
	 * @return
	 */
	public boolean removeCookie(HttpServletRequest request,HttpServletResponse response,String name){
		Cookie cookie = getCookie(request,name);
		if(cookie != null){
			cookie.setMaxAge(0);
			cookie.setPath("/");
			/**
			 * 设置 cookie的MaxAge为0时，必须也要设置 setPath，不然不会失效
			 */
			response.addCookie(cookie);

			return true;
		}
		return false;
	}
}
