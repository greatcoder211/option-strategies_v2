package ownStrategy.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ownStrategy.exceptions.KeyWordException;
import ownStrategy.exceptions.QuantityException;
import ownStrategy.exceptions.SpreadException;
import ownStrategy.exceptions.TickerNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GreatExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> validationExceptions(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        List<FieldError> allErrors = ex.getBindingResult().getFieldErrors();
        for(FieldError error : allErrors){
            errors.put(error.getField(),error.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler(TickerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // To zamieni 500 na 404 w Twoim .http
    public Map<String, String> tickerExceptions(TickerNotFoundException ex){
    Map<String,String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        errors.put("code", "TICKER_NOT_FOUND");
        return errors;
    }

    @ExceptionHandler(KeyWordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> keyWordExceptions(KeyWordException ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        errors.put("code", "KEY_WORD_TOO_SHORT");
        return errors;
    }


    @ExceptionHandler({QuantityException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> quantityExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException mismatch = (MethodArgumentTypeMismatchException) ex;
            if ("quant".equals(mismatch.getName())) {
                errors.put("message", ex.getMessage());
                errors.put("code", "WRONG_QUANTITY");
                return errors;
            }
        }
        errors.put("message", ex.getMessage());
        errors.put("code", "WRONG_QUANTITY");
        return errors;
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> spreadCheck(SpreadException ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("spreads", ex.getMessage());
        errors.put("code", "INVALID_SPREADS");
        return errors;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Invalid username or password");
        errors.put("code", "AUTH_FAILED");
        return errors;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "You do not have permission to access this resource");
        errors.put("code", "ACCESS_DENIED");
        return errors;
    }
}
