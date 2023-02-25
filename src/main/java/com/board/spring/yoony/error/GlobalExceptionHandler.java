package com.board.spring.yoony.error;

import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

/**
 * 설명
 *
 * @author YK
 * @version 1.0
 * @fileName GlobalExceptionHandler
 * @since 2023-02-24
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomExceptionView.class)
  public ModelAndView handleCustomExceptionView(CustomExceptionView ex) {
    log.error("handleCustomExceptionView", ex);

    ModelAndView modelAndView = new ModelAndView();
    ex.getErrorCode().getErrorCode();

    modelAndView.addObject("exception", ex);
    modelAndView.setViewName("error/error");
    return modelAndView;
  }
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
    log.error("handleCustomException", ex);
    ErrorResponse response = new ErrorResponse(ex.getErrorCode());
    return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleException(Exception ex) {
    log.error("handleException", ex);
    ModelAndView modelAndView = new ModelAndView();

    modelAndView.addObject("exception", ex);
    modelAndView.setViewName("error/error");
    return modelAndView;
  }
}