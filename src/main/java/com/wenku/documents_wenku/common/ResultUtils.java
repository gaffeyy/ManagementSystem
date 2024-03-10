package com.wenku.documents_wenku.common;

/**
 * 返回封装类
 *
 */
public class ResultUtils {
	/**
	 * 请求成功
	 *
	 * @param data
	 * @param description
	 * @return BaseResponse
	 * @param <T>
	 */
	public static <T> BaseResponse<T> success(T data,String description){
		return new BaseResponse<>(BusinessErrors.SUCCESS.getCode(),data,
				BusinessErrors.SUCCESS.getMessage(),description);
	}

	/**
	 * 请求失败
	 *
	 * @param businessErrors
	 * @param description
	 * @return BaseResponse
	 */
	public static BaseResponse error(BusinessErrors businessErrors,String description){
		return new BaseResponse(businessErrors,description);
	}

	/**
	 * 请求失败
	 *
	 * @param businessErrors
	 * @return BaseResponse
	 */
	public static BaseResponse error(BusinessErrors businessErrors){
		return new BaseResponse<>(businessErrors);
	}
	public static BaseResponse error(int code,String message,String description){
		return new BaseResponse<>(code,null,message,description);
	}
}
