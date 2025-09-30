package com.TaleScoper.TaleScoper.jwt;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.TaleScoper.TaleScoper.exception.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		ObjectMapper mapper = new ObjectMapper();
		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.FORBIDDEN.value())
				.error("Forbidden")
				.message("Access denied")
				.path(request.getRequestURI())
				.timestamp(Instant.now())
				.build();

		response.getWriter().write(mapper.writeValueAsString(errorResponse));
	}
}