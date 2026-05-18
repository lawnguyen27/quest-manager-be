package com.example.common.config.exception;

import com.example.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ApiResponse<Void> handleBadRequest(BadRequestException ex) {
		log.error("Bad Request Exception: {}", ex.getMessage());
		return ApiResponse.error(400, ex.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ApiResponse<Void> handleNotFound(NotFoundException ex) {
		log.error("Not Found Exception: {}", ex.getMessage());
		return ApiResponse.error(404, ex.getMessage());
	}

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse<Void> handleGeneralException(Exception ex) {
		log.error("Internal Server Error: ", ex);
        return ApiResponse.error(500, "Internal Server Error: " + ex.getMessage());
    }
}
