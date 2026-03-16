package ytg.libros.adapter.in.dto;

public record LibroResponse(
        Long id,
        String titulo,
        String autor,
        Double precio
) {
}
