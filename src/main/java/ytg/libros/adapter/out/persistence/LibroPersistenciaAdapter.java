package ytg.libros.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import ytg.libros.domain.model.Libro;
import ytg.libros.domain.port.out.LibroRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LibroPersistenciaAdapter implements LibroRepositoryPort {

    private final LibroJpaRepository repository;

    @Override
    public Libro guardarLibro(Libro libro) {

        LibroEntity entity = LibroMapper.toEntity(libro);

        LibroEntity saved = repository.save(entity);

        return LibroMapper.toDomain(saved);
    }

    @Override
    public Optional<Libro> traerLibroById(Long id) {

        return repository.findById(id)
                .map(LibroMapper::toDomain);
    }

    @Override
    public List<Libro> traerTodoLibros() {

        return repository.findAll()
                .stream()
                .map(LibroMapper::toDomain)
                .toList();
    }
}
