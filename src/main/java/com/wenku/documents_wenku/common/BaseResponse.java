package com.wenku.documents_wenku.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author gaffey
 */
@Data
public class BaseResponse<T> implements Serializable {
	private int code;
	private T data;
	private String message;
	private String description;

	public BaseResponse(int code,T data,String message,String description){
		this.code = code;
		this.data = data;
		this.message = message;
		this.description = description;
	}

	public BaseResponse(int code,T data,String message){
		this(code,data,message,"没有具体描述");
	}

	public BaseResponse(int code,String message,String description){
		this(code,null,message,description);
	}

	public BaseResponse(BusinessErrors businessErrors){
		this.code = businessErrors.getCode();
		this.message = businessErrors.getMessage();
		this.description = businessErrors.getDescription();
	}

	public BaseResponse(BusinessErrors businessErrors,String description){
		this.code = businessErrors.getCode();
		this.message = businessErrors.getMessage();
		this.description = description;
	}


}
