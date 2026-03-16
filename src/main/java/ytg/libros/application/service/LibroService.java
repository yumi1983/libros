package ytg.libros.application.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ytg.libros.domain.exception.LibroNoEncontradoException;
import ytg.libros.domain.exception.ValidacionLibroException;
import ytg.libros.domain.model.Libro;
import ytg.libros.domain.port.in.ActualizarLibroUseCase;
import ytg.libros.domain.port.in.CrearLibroUseCase;
import ytg.libros.domain.port.in.TraerLibroUseCase;
import ytg.libros.domain.port.out.LibroRepositoryPort;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class LibroService implements CrearLibroUseCase, TraerLibroUseCase, ActualizarLibroUseCase {

    private final LibroRepositoryPort libroRepository;

    public LibroService(LibroRepositoryPort libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public Mono<Libro> crearLibro(Libro libro) {
        Supplier<ValidacionLibroException> invalidLibro =
                () -> new ValidacionLibroException("El libro no es valido");

        Consumer<Libro> validarLibro = candidate -> {
            Libro libroValido = Optional.ofNullable(candidate)
                    .orElseThrow(invalidLibro);

            Optional.ofNullable(libroValido.titulo())
                    .map(String::trim)
                    .filter(texto -> !texto.isBlank())
                    .orElseThrow(() -> new ValidacionLibroException("El titulo es obligatorio"));

            Optional.ofNullable(libroValido.autor())
                    .map(String::trim)
                    .filter(texto -> !texto.isBlank())
                    .orElseThrow(() -> new ValidacionLibroException("El autor es obligatorio"));

            Optional.ofNullable(libroValido.precio())
                    .filter(precio -> precio > 0)
                    .orElseThrow(() -> new ValidacionLibroException("El precio debe ser mayor que cero"));
        };

        return Mono.fromCallable(() -> {
                    validarLibro.accept(libro);
                    return libroRepository.guardarLibro(libro);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Libro> traerLibroById(Long id) {
        return Mono.fromCallable(() -> Optional.ofNullable(id)
                        .flatMap(libroRepository::traerLibroById)
                        .orElseThrow(() -> new LibroNoEncontradoException(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Libro> traerTodoLibros() {
        return Mono.fromCallable(libroRepository::traerTodoLibros)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(libros -> Flux.fromIterable(
                        libros.stream()
                                .map(this::normalizarTexto)
                                .toList()
                ));
    }

    @Override
    public Flux<Libro> traerLibroPorPrecioMayorQue(Double precio) {
        return traerTodoLibros().filter(precioMayorQue(precio));
    }

    @Override
    public Mono<Libro> actualizarLibroParcial(Long id, Libro libroParcial) {
        return Mono.fromCallable(() -> {
                    Libro existente = Optional.ofNullable(id)
                            .flatMap(libroRepository::traerLibroById)
                            .orElseThrow(() -> new LibroNoEncontradoException(id));

                    validarPatch(libroParcial);
                    return libroRepository.guardarLibro(mergeLibro(id, existente, libroParcial));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }


    private Predicate<Libro> precioMayorQue(Double precio) {
        return libro -> Optional.ofNullable(libro.precio())
                .map(valor -> valor > precio)
                .orElse(false);
    }

    private Libro normalizarTexto(Libro libro) {
        return new Libro(
                libro.id(),
                Optional.ofNullable(libro.titulo()).map(String::trim).orElse(""),
                Optional.ofNullable(libro.autor()).map(String::trim).orElse(""),
                libro.precio()
        );
    }

    private void validarPatch(Libro libroParcial) {
        Libro patch = Optional.ofNullable(libroParcial)
                .orElseThrow(() -> new ValidacionLibroException("El cuerpo de la solicitud no es valido"));

        boolean sinCambios = patch.titulo() == null && patch.autor() == null && patch.precio() == null;
        if (sinCambios) {
            throw new ValidacionLibroException("Debe enviar al menos un campo para actualizar");
        }

        if (patch.titulo() != null && patch.titulo().trim().isBlank()) {
            throw new ValidacionLibroException("El titulo no puede estar vacio");
        }

        if (patch.autor() != null && patch.autor().trim().isBlank()) {
            throw new ValidacionLibroException("El autor no puede estar vacio");
        }

        if (patch.precio() != null && patch.precio() <= 0) {
            throw new ValidacionLibroException("El precio debe ser mayor que cero");
        }
    }

    private Libro mergeLibro(Long id, Libro existente, Libro patch) {
        return new Libro(
                id,
                Optional.ofNullable(patch.titulo()).map(String::trim).orElse(existente.titulo()),
                Optional.ofNullable(patch.autor()).map(String::trim).orElse(existente.autor()),
                Optional.ofNullable(patch.precio()).orElse(existente.precio())
        );
    }
}
