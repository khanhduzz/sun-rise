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
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(NotFoundException ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(
            HttpStatus.NOT_FOUND.toString(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            message
        );
        ModelAndView modelAndView = new ModelAndView(getServletPath(request));
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("error", errorVm);
        return modelAndView;
    }

    @ExceptionHandler(BadRequestException.class)
    public ModelAndView handleBadRequestException(BadRequestException ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(
            HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            message
        );
        ModelAndView modelAndView = new ModelAndView(getServletPath(request));
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("error", errorVm);
        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ModelAndView handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();
        ErrorVm errorVm = new ErrorVm(
            HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Request information is not valid",
            errors
        );
        ModelAndView modelAndView = new ModelAndView(getServletPath(request));
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("error", errorVm);
        return modelAndView;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " "
                + violation.getPropertyPath() + ": " + violation.getMessage());
        }
        ErrorVm errorVm = new ErrorVm(
            HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Request information is not valid",
            errors
        );
        ModelAndView modelAndView = new ModelAndView(getServletPath(request));
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("error", errorVm);
        return modelAndView;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException e, WebRequest request) {
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        ErrorVm errorVm = new ErrorVm(
            HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            message
        );
        ModelAndView modelAndView = new ModelAndView(getServletPath(request));
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("error", errorVm);
        return modelAndView;
    }

    @ExceptionHandler(DuplicatedException.class)
    protected ModelAndView handleDuplicated(DuplicatedException e, WebRequest request) {
        ErrorVm errorVm = new ErrorVm(
            HttpStatus.BAD_REQUEST.toString(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            e.getMessage()
        );
        ModelAndView modelAndView = new ModelAndView(getServletPath(request));
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("error", errorVm);
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    protected ModelAndView handleOtherException(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(
            HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            message
        );
        ModelAndView modelAndView = new ModelAndView(getServletPath(request));
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("error", errorVm);
        return modelAndView;
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }
}
