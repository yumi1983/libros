package ytg.libros.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ytg.libros.domain.exception.LibroNoEncontradoException;
import ytg.libros.domain.exception.ValidacionLibroException;
import ytg.libros.domain.model.Libro;
import ytg.libros.domain.port.out.LibroRepositoryPort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepositoryPort libroRepository;

    @InjectMocks
    private LibroService libroService;

    @Test
    void crearLibro_debePersistirCuandoEsValido() {
        Libro request = new Libro(null, "Clean Code", "Robert Martin", 120.0);
        Libro persisted = new Libro(1L, "Clean Code", "Robert Martin", 120.0);

        when(libroRepository.guardarLibro(any(Libro.class))).thenReturn(persisted);

        Libro resultado = libroService.crearLibro(request).block();

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    void crearLibro_debeFallarCuandoEsInvalido() {
        Libro request = new Libro(null, "", "Autor", 10.0);

        assertThatThrownBy(() -> libroService.crearLibro(request).block())
                .satisfies(error -> assertThat(rootCause(error)).isInstanceOf(ValidacionLibroException.class));
    }

    @Test
    void traerLibroById_debeFallarCuandoNoExiste() {
        when(libroRepository.traerLibroById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libroService.traerLibroById(99L).block())
                .satisfies(error -> assertThat(rootCause(error)).isInstanceOf(LibroNoEncontradoException.class));
    }

    @Test
    void traerLibroPorPrecioMayorQue_debeFiltrarConPredicate() {
        when(libroRepository.traerTodoLibros()).thenReturn(List.of(
                new Libro(1L, "A", "Autor A", 10.0),
                new Libro(2L, "B", "Autor B", 40.0),
                new Libro(3L, "C", "Autor C", 70.0)
        ));

        List<Libro> libros = libroService.traerLibroPorPrecioMayorQue(30.0)
                .collectList()
                .block();

        assertThat(libros).isNotNull();
        assertThat(libros).hasSize(2);
        assertThat(libros).extracting(Libro::id).containsExactly(2L, 3L);
    }

    @Test
    void actualizarLibroParcial_debeActualizarSoloCamposEnviados() {
        Libro existente = new Libro(10L, "Titulo viejo", "Autor viejo", 25.0);
        Libro actualizado = new Libro(10L, "Titulo nuevo", "Autor viejo", 40.0);

        when(libroRepository.traerLibroById(10L)).thenReturn(Optional.of(existente));
        when(libroRepository.guardarLibro(any(Libro.class))).thenReturn(actualizado);

        Libro patch = new Libro(null, "Titulo nuevo", null, 40.0);
        Libro resultado = libroService.actualizarLibroParcial(10L, patch).block();

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.titulo()).isEqualTo("Titulo nuevo");
        assertThat(resultado.autor()).isEqualTo("Autor viejo");
        assertThat(resultado.precio()).isEqualTo(40.0);
    }

    @Test
    void actualizarLibroParcial_debeFallarCuandoPatchEstaVacio() {
        when(libroRepository.traerLibroById(10L)).thenReturn(Optional.of(new Libro(10L, "Titulo", "Autor", 25.0)));

        assertThatThrownBy(() -> libroService.actualizarLibroParcial(10L, new Libro(null, null, null, null)).block())
                .satisfies(error -> assertThat(rootCause(error)).isInstanceOf(ValidacionLibroException.class));
    }

    @Test
    void actualizarLibroParcial_debeFallarCuandoNoExisteLibro() {
        when(libroRepository.traerLibroById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libroService.actualizarLibroParcial(999L, new Libro(null, "Nuevo", null, null)).block())
                .satisfies(error -> assertThat(rootCause(error)).isInstanceOf(LibroNoEncontradoException.class));
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

}

