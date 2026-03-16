package ytg.libros.adapter.in.dto;

public record LibroPatchRequest(
        String titulo,
        String autor,
        Double precio
) {
}

