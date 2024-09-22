package com.fjb.sunrise.exceptions;

import com.fjb.sunrise.dtos.responses.ErrorVm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";
    private static final String ERROR = "error";

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(NotFoundException ex, WebRequest request) {
        String message = ex.getMessage();

        ErrorVm errorVm = new ErrorVm("404",
            HttpStatus.NOT_FOUND.getReasonPhrase(), message);

        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 404, message);
        log.debug(ex.toString());

        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(ERROR, errorVm);
        modelAndView.setStatus(HttpStatus.NOT_FOUND);

        return modelAndView;
    }

    @ExceptionHandler(BadRequestException.class)
    public ModelAndView handleBadRequestException(BadRequestException ex, WebRequest request) {
        String message = ex.getMessage();

        ErrorVm errorVm = new ErrorVm("400",
            HttpStatus.BAD_REQUEST.getReasonPhrase(), message);

        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 400, message);
        log.debug(ex.toString());

        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(ERROR, errorVm);
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);

        return modelAndView;
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

    @ExceptionHandler({DuplicatedException.class, FailedSendMailException.class})
    protected String handleDuplicated(Exception e, RedirectAttributes redirectAttributes,
                                      WebRequest request, HttpServletRequest httpRequest) {
        String message = e.getMessage();
        ErrorVm errorVm = new ErrorVm("400",
            HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
        redirectAttributes.addFlashAttribute(ERROR, errorVm);

        log.warn(ERROR_LOG_FORMAT, request.getDescription(false), 400, message);
        log.debug(e.toString());

        String currentUri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        if (currentUri.startsWith(contextPath)) {
            currentUri = currentUri.substring(contextPath.length());
        }

        return "redirect:" + (currentUri.isEmpty() ? "/error" : currentUri);
    }

    @ExceptionHandler(Exception.class)
    protected ModelAndView handleOtherException(Exception ex, WebRequest request) {
        String message = ex.getMessage();

        ErrorVm errorVm = new ErrorVm("500",
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), message);

        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 500, message);
        log.debug(ex.toString());

        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(ERROR, errorVm);
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        return modelAndView;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String message = "The requested resource was not found";

        ErrorVm errorVm = new ErrorVm("404",
            HttpStatus.NOT_FOUND.getReasonPhrase(), message);

        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(ERROR, errorVm);
        modelAndView.setStatus(HttpStatus.NOT_FOUND);

        return modelAndView;
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ModelAndView handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        String message = ex.getMessage();

        ErrorVm errorVm = new ErrorVm("401",
            HttpStatus.UNAUTHORIZED.getReasonPhrase(), message);

        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 401, message);
        log.debug(ex.toString());

        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(ERROR, errorVm);
        modelAndView.setStatus(HttpStatus.UNAUTHORIZED);

        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ModelAndView handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String message = ex.getMessage();

        ErrorVm errorVm = new ErrorVm("403",
            HttpStatus.FORBIDDEN.getReasonPhrase(), message);

        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 403, message);
        log.debug(ex.toString());

        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(ERROR, errorVm);
        modelAndView.setStatus(HttpStatus.FORBIDDEN);

        return modelAndView;
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }
}
