package com.wenku.documents_wenku.exception;

import com.wenku.documents_wenku.common.BaseResponse;
import com.wenku.documents_wenku.common.BusinessErrors;
import com.wenku.documents_wenku.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public BaseResponse BusinessExceptionHandler(BusinessException businessException){
		log.error("BusinessException ---- "+businessException.getMessage(),businessException);
		return ResultUtils.error(businessException.getCode(), businessException.getMessage(), businessException.getDescription());
	}

	@ExceptionHandler(RuntimeException.class)
	public BaseResponse RuntimeExceptionHandler(RuntimeException e){
		log.error("RuntimeException ---- "+e.getMessage(),e);
		return ResultUtils.error(BusinessErrors.SYSTEM_ERROR);
	}
}
