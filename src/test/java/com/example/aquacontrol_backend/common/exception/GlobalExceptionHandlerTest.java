package com.example.aquacontrol_backend.common.exception;

import com.example.aquacontrol_backend.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private HttpServletRequest request;
    @BeforeEach void preparar() {
        handler=new GlobalExceptionHandler(); request=mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/prueba");
        when(request.getMethod()).thenReturn("GET");
    }
    @Test void recursoNoEncontradoDevuelve404() {
        var r=handler.notFound(new ResourceNotFoundException("No existe"),request);
        assertEquals(HttpStatus.NOT_FOUND,r.getStatusCode()); assertNotNull(r.getBody());
    }
    @Test void credencialesIncorrectasDevuelven401() {
        var r=handler.authenticationError(new BadCredentialsException("incorrecta"),request);
        assertEquals(HttpStatus.UNAUTHORIZED,r.getStatusCode());
    }
    @Test void argumentoInvalidoDevuelve400() {
        var r=handler.invalidRequest(new IllegalArgumentException("invalido"),request);
        assertEquals(HttpStatus.BAD_REQUEST,r.getStatusCode());
    }
    @Test void archivoFaltanteDevuelve400() {
        var r=handler.missingFile(new MissingServletRequestPartException("foto"),request);
        assertEquals(HttpStatus.BAD_REQUEST,r.getStatusCode());
    }
    @Test void archivoGrandeDevuelve413() {
        var r=handler.uploadTooLarge(new MaxUploadSizeExceededException(100L),request);
        assertEquals(HttpStatus.CONTENT_TOO_LARGE,r.getStatusCode());
    }
    @Test void excepcionImprevistaDevuelve500() {
        var r=handler.unexpected(new RuntimeException("detalle privado"),request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,r.getStatusCode());
    }
}
