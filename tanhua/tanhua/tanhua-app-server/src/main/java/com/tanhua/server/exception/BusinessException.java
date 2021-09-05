package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private ErrorResult errorResult;

    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}