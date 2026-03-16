package ytg.libros.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LibroRequest(
    @NotBlank
    String titulo,

    @NotBlank
    String autor,

    @NotNull
    Double precio
) {}
