package ytg.libros.domain.port.in;

import ytg.libros.domain.model.Libro;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TraerLibroUseCase {
    Mono<Libro> traerLibroById(Long id);
    Flux<Libro> traerTodoLibros();
    Flux<Libro> traerLibroPorPrecioMayorQue(Double precio);
}
