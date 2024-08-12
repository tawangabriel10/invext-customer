package br.com.invext.customer.config;

import br.com.invext.customer.domain.dto.ResponseDTO;
import br.com.invext.customer.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDTO businessException(final BusinessException ex) {
        return ResponseDTO.builder()
            .response(ex.getMessage())
            .build();
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDTO httpMessageNotReadableException(final HttpMessageNotReadableException ex) {
        return ResponseDTO.builder()
            .response(ex.getMessage())
            .build();
    }
}
