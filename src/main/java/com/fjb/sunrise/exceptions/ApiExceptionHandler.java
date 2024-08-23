package com.fjb.sunrise.exceptions;

import com.fjb.sunrise.dtos.responses.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

    @ExceptionHandler(NotFoundException.class)
    public ErrorVm handleNotFoundException(NotFoundException ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.NOT_FOUND.toString(),
            HttpStatus.NOT_FOUND.getReasonPhrase(), message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 404, message);
        log.debug(ex.toString());
        return errorVm;
    }

    @ExceptionHandler(BadRequestException.class)
    public ErrorVm handleBadRequestException(BadRequestException ex,
                                                             WebRequest request) {
        String message = ex.getMessage();
        return new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorVm handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();

        return new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(), "Request information is not valid", errors);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ErrorVm handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " "
                + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        return new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(), "Request information is not valid", errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorVm handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        return new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
    }

    @ExceptionHandler(DuplicatedException.class)
    protected ErrorVm handleDuplicated(DuplicatedException e) {
        return new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    protected ErrorVm handleOtherException(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 500, message);
        log.debug(ex.toString());
        return errorVm;
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }
}
