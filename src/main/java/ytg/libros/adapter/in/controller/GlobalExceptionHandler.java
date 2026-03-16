package ytg.libros.adapter.in.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.Exceptions;
import ytg.libros.adapter.in.dto.ErrorResponse;
import ytg.libros.domain.exception.LibroNoEncontradoException;
import ytg.libros.domain.exception.ValidacionLibroException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LibroNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(LibroNoEncontradoException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage(), "", Instant.now()));
    }

    @ExceptionHandler(ValidacionLibroException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidacionLibroException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage(), "", Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception exception) {
        Throwable root = resolveRootCause(Exceptions.unwrap(exception));

        if (root instanceof ErrorResponseException errorResponseException) {
            String message = errorResponseException.getMessage() != null
                    ? errorResponseException.getMessage()
                    : "Recurso no encontrado";
            return ResponseEntity.status(errorResponseException.getStatusCode())
                    .body(new ErrorResponse(message, "", Instant.now()));
        }

        if (root instanceof ResponseStatusException responseStatusException) {
            String message = responseStatusException.getReason() != null
                    ? responseStatusException.getReason()
                    : "Solicitud invalida";

            return ResponseEntity.status(responseStatusException.getStatusCode())
                    .body(new ErrorResponse(message, "", Instant.now()));
        }

        if (root instanceof LibroNoEncontradoException notFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(notFound.getMessage(), "", Instant.now()));
        }

        if (root instanceof ValidacionLibroException validation) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(validation.getMessage(), "", Instant.now()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error interno del servidor", "", Instant.now()));
    }

    private Throwable resolveRootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}

