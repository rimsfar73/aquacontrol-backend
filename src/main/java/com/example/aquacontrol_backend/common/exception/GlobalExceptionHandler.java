package com.example.aquacontrol_backend.common.exception;

import com.example.aquacontrol_backend.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

// Centralizamos las respuestas de error para que Android reciba un formato común.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Incluimos fecha, código HTTP, mensaje y dirección de la solicitud.
    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> details
    ) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
        ));
    }

    // Respondemos 404 cuando no existe el registro solicitado.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                Map.of()
        );
    }

    // Indicamos los campos que no cumplen las validaciones del DTO.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidBody(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        return response(
                HttpStatus.BAD_REQUEST,
                "Datos de entrada inválidos",
                request,
                errors
        );
    }

    // Respondemos 413 si se supera el límite de archivo o del formulario.
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> uploadTooLarge(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.valueOf(413),
                "La carga supera el tamaño permitido: "
                        + "máximo 5 MiB por foto y 6 MiB por solicitud.",
                request,
                Map.of()
        );
    }

    // Avisamos si el formulario no incluye el archivo solicitado.
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> missingFile(
            MissingServletRequestPartException ex,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "Falta un archivo requerido en el formulario.",
                request,
                Map.of("campo", ex.getRequestPartName())
        );
    }

    // Conservamos la respuesta para datos incorrectos o formatos no válidos.
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> invalidRequest(
            Exception ex,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida: revise los datos enviados",
                request,
                Map.of()
        );
    }

    // Conservamos el manejo de credenciales incorporado por Rimsky.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationError(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas",
                request,
                Map.of()
        );
    }

    // Evitamos exponer detalles internos ante errores inesperados.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                request,
                Map.of()
        );
    }
}