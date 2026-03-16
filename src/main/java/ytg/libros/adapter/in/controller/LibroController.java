package ytg.libros.adapter.in.controller;

import lombok.RequiredArgsConstructor;
import ytg.libros.adapter.in.dto.LibroPatchRequest;
import ytg.libros.adapter.in.dto.LibroRequest;
import ytg.libros.adapter.in.dto.LibroResponse;
import ytg.libros.domain.model.Libro;
import ytg.libros.domain.port.in.ActualizarLibroUseCase;
import ytg.libros.domain.port.in.CrearLibroUseCase;
import ytg.libros.domain.port.in.TraerLibroUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/libros")
@RequiredArgsConstructor
public class LibroController {
    private final CrearLibroUseCase crearLibroUseCase;
    private final TraerLibroUseCase traerLibroUseCase;
    private final ActualizarLibroUseCase actualizarLibroUseCase;

    @PostMapping
    public Mono<ResponseEntity<LibroResponse>> crearLibro(
            @Valid @RequestBody LibroRequest request) {

        Libro libro = new Libro(
                null,
                request.titulo(),
                request.autor(),
                request.precio()
        );

        return crearLibroUseCase.crearLibro(libro)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<LibroResponse>> traerLibroById(@PathVariable Long id) {
        return traerLibroUseCase.traerLibroById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<java.util.List<LibroResponse>>> getAllBooks() {
        return traerLibroUseCase.traerTodoLibros()
                .map(this::toResponse)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<LibroResponse>> actualizarLibroParcial(
            @PathVariable Long id,
            @RequestBody LibroPatchRequest request
    ) {
        Libro patch = new Libro(
                id,
                request.titulo(),
                request.autor(),
                request.precio()
        );

        return actualizarLibroUseCase.actualizarLibroParcial(id, patch)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    private LibroResponse toResponse(Libro libro) {
        return new LibroResponse(
                libro.id(),
                libro.titulo(),
                libro.autor(),
                libro.precio()
        );
    }
}
