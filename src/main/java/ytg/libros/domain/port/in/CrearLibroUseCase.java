package ytg.libros.domain.port.in;

import ytg.libros.domain.model.Libro;
import reactor.core.publisher.Mono;

public interface CrearLibroUseCase {
    Mono<Libro> crearLibro(Libro libro);
}
