package ytg.libros.domain.model;

public record Libro(
        Long id,
        String titulo,
        String autor,
        Double precio
) {
}
