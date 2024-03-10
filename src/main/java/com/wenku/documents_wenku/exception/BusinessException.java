package com.wenku.documents_wenku.exception;

import com.wenku.documents_wenku.common.BusinessErrors;
import lombok.Data;

/**
 *自定义业务异常类
 *
 * @author gaffey
 */
@Data
public class BusinessException extends RuntimeException{
	/**
	 * 异常描述
	 */
	private String description;

	/**
	 * 异常码
	 */
	private int code;

	public BusinessException(BusinessErrors businessErrors,String description){
		super(businessErrors.getMessage());
		this.description = description;
		this.code = businessErrors.getCode();
	}
	public BusinessException(BusinessErrors businessErrors){
		super(businessErrors.getMessage());
		this.code = businessErrors.getCode();
		this.description = businessErrors.getDescription();
	}
}
