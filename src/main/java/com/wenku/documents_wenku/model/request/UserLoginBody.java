package com.wenku.documents_wenku.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 */
@Data
public class UserLoginBody implements Serializable {
	String userAccount;
	String userPassword;
}
