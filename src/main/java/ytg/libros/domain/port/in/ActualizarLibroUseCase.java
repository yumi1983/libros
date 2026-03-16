package ytg.libros.domain.port.in;

import reactor.core.publisher.Mono;
import ytg.libros.domain.model.Libro;

public interface ActualizarLibroUseCase {
    Mono<Libro> actualizarLibroParcial(Long id, Libro libroParcial);
}

