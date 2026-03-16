package ytg.libros.adapter.out.persistence;

import ytg.libros.domain.model.Libro;

public class LibroMapper {

    public static LibroEntity toEntity(Libro libro) {

        return new LibroEntity(
                libro.id(),
                libro.titulo(),
                libro.autor(),
                libro.precio()
        );
    }

    public static Libro toDomain(LibroEntity entity) {

        return new Libro(
                entity.getId(),
                entity.getTitulo(),
                entity.getAutor(),
                entity.getPrecio()
        );
    }
}
