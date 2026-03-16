package ytg.libros.adapter.in.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ytg.libros.domain.exception.LibroNoEncontradoException;
import ytg.libros.domain.exception.ValidacionLibroException;
import ytg.libros.domain.model.Libro;
import ytg.libros.domain.port.in.ActualizarLibroUseCase;
import ytg.libros.domain.port.in.CrearLibroUseCase;
import ytg.libros.domain.port.in.TraerLibroUseCase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = LibroController.class)
@Import(GlobalExceptionHandler.class)
class LibroControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CrearLibroUseCase crearLibroUseCase;

    @MockitoBean
    private TraerLibroUseCase traerLibroUseCase;

    @MockitoBean
    private ActualizarLibroUseCase actualizarLibroUseCase;


    @Test
    void crearLibro_debeRetornar200() {
        when(crearLibroUseCase.crearLibro(any(Libro.class)))
                .thenReturn(Mono.just(new Libro(1L, "1984", "George Orwell", 20.0)));

        webTestClient.post()
                .uri("/libros")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          \"titulo\": \"1984\",
                          \"autor\": \"George Orwell\",
                          \"precio\": 20.0
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.titulo").isEqualTo("1984");
    }

    @Test
    void traerLibroById_cuandoNoExiste_debeRetornar404() {
        when(traerLibroUseCase.traerLibroById(99L)).thenReturn(Mono.error(new LibroNoEncontradoException(99L)));

        webTestClient.get()
                .uri("/libros/99")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("No existe un libro con id 99");
    }

    @Test
    void traerLibroById_cuandoExcepcionEnvolvente_debeRetornar404() {
        when(traerLibroUseCase.traerLibroById(99999L))
                .thenReturn(Mono.error(new RuntimeException(new LibroNoEncontradoException(99999L))));

        webTestClient.get()
                .uri("/libros/99999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("No existe un libro con id 99999");
    }

    @Test
    void listarLibros_debeRetornarColeccion() {
        when(traerLibroUseCase.traerTodoLibros()).thenReturn(Flux.just(
                new Libro(1L, "A", "Autor A", 10.0),
                new Libro(2L, "B", "Autor B", 30.0)
        ));

        webTestClient.get()
                .uri("/libros")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[1].id").isEqualTo(2);
    }

    @Test
    void actualizarLibroParcial_debeRetornar200() {
        when(actualizarLibroUseCase.actualizarLibroParcial(any(Long.class), any(Libro.class)))
                .thenReturn(Mono.just(new Libro(10L, "Titulo nuevo", "Autor", 50.0)));

        webTestClient.patch()
                .uri("/libros/10")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "titulo": "Titulo nuevo",
                          "precio": 50.0
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.titulo").isEqualTo("Titulo nuevo")
                .jsonPath("$.autor").isEqualTo("Autor")
                .jsonPath("$.precio").isEqualTo(50.0);
    }

    @Test
    void actualizarLibroParcial_cuandoPatchInvalido_debeRetornar400() {
        when(actualizarLibroUseCase.actualizarLibroParcial(any(Long.class), any(Libro.class)))
                .thenReturn(Mono.error(new ValidacionLibroException("Debe enviar al menos un campo para actualizar")));

        webTestClient.patch()
                .uri("/libros/10")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Debe enviar al menos un campo para actualizar");
    }
}

