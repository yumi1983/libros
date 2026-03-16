package ytg.libros.domain.port.out;

import ytg.libros.domain.model.Libro;

import java.util.List;
import java.util.Optional;

public interface LibroRepositoryPort {
    Libro guardarLibro(Libro libro);
    Optional<Libro> traerLibroById(Long id);
    List<Libro> traerTodoLibros();
}
