package vnua.edu.xdptpm09.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.mail.SendFailedException;
import jakarta.validation.ConstraintViolationException;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.PathElementException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import vnua.edu.xdptpm09.dto.ErrorResponse;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandleException {
    @ExceptionHandler({ResourceNotFoundException.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleExceptionNotFound(Exception e, WebRequest request) {
        return ErrorResponse.builder().timestamp(new Date()).status(HttpStatus.NOT_FOUND.value()).path(request.getDescription(false).replace("uri=", "")).message(e.getMessage()).error(HttpStatus.NOT_FOUND.getReasonPhrase()).build();
    }

    @ExceptionHandler({InvalidVideoFormatException.class, SendFailedException.class, JWTDecodeException.class, NumberFormatException.class, PathElementException.class, InvalidFormatException.class, MissingServletRequestParameterException.class, UploadFailureException.class, MaxUploadSizeExceededException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFileException(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            message = message.substring(start + 1, end - 1);
            return ErrorResponse.builder().timestamp(new Date()).status(HttpStatus.BAD_REQUEST.value()).path(request.getDescription(false).replace("uri=", "")).message(message).error(HttpStatus.NOT_FOUND.getReasonPhrase()).build();
        } else if (ex instanceof ConstraintViolationException) {
            message = ex.getMessage().split(":")[1];
            return ErrorResponse.builder().timestamp(new Date()).status(HttpStatus.BAD_REQUEST.value()).path(request.getDescription(false).replace("uri=", "")).message(message).error(HttpStatus.NOT_FOUND.getReasonPhrase()).build();
        } else {
            return ex instanceof MaxUploadSizeExceededException ? ErrorResponse.builder().timestamp(new Date()).status(HttpStatus.BAD_REQUEST.value()).path(request.getDescription(false).replace("uri=", "")).message(message).error(HttpStatus.BAD_REQUEST.getReasonPhrase()).build() : ErrorResponse.builder().timestamp(new Date()).status(HttpStatus.BAD_REQUEST.value()).path(request.getDescription(false).replace("uri=", "")).message(ex.getMessage()).error(HttpStatus.BAD_REQUEST.getReasonPhrase()).build();
        }
    }

    @ExceptionHandler({UnauthorizedException.class, TokenExpiredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(Exception e, WebRequest request) {
        return ErrorResponse.builder().timestamp(new Date()).status(HttpStatus.UNAUTHORIZED.value()).path(request.getDescription(false).replace("uri=", "")).message(e.getMessage()).error(HttpStatus.UNAUTHORIZED.getReasonPhrase()).build();
    }
}
