package com.wenku.documents_wenku.common;

/**
 * 自定义错误码
 */
public enum BusinessErrors {
	SUCCESS(20000,"请求成功",""),
	PARAMS_ERROR(40000,"请求参数错误",""),
	NULL_ERROR(40001,"请求数据为空",""),
	NOT_LOGIN(40100,"未登录",""),
	NOT_ADMIN(40101,"无权限",""),
	SYSTEM_ERROR(50000,"系统内部错误",""),

	;

	/**
	 * 错误码
	 */
	private int code;
	/**
	 * 错误信息
	 */
	private String message;
	/**
	 * 错误具体描述
	 */
	private String description;

	BusinessErrors(int code,String message,String description){
		this.code = code;
		this.message = message;
		this.description = description;
	}
	public int getCode() {
		return code;
	}

	public String getMessage(){
		return message;
	}

	public String getDescription(){
		return description;
	}


}
