package ytg.libros.adapter.in.dto;

import java.time.Instant;

public record ErrorResponse(
        String message,
        String path,
        Instant timestamp
) {
}

