package com.wenku.documents_wenku.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterBody implements Serializable {
	String userAccount;
	String userPassword;
	String checkPassword;
}
