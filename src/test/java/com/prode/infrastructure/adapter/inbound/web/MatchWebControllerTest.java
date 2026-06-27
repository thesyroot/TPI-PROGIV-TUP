package com.prode.infrastructure.adapter.inbound.web;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.prode.application.dto.request.MatchRequest;
import com.prode.application.dto.response.MatchResponse;
import com.prode.application.dto.response.RoundResponse;
import com.prode.application.service.MatchService;
import com.prode.application.service.RoundService;
import com.prode.application.service.TeamService;

@WebMvcTest(controllers = MatchWebController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchWebControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private MatchService matchService;

        @MockitoBean
        private RoundService roundService;

        @MockitoBean
        private TeamService teamService;

        // Seguridad
        @MockitoBean
        private com.prode.infrastructure.security.JwtService jwtService;

        @MockitoBean
        private com.prode.infrastructure.security.UserDetailsServiceImpl userDetailsService;

        @MockitoBean
        private com.prode.infrastructure.security.TokenBlacklistService tokenBlacklistService;

        // -----------------------------------------------------------------------
        // Datos reutilizables (@BeforeEach - sección 5.2 del PDF)
        // -----------------------------------------------------------------------

        private MatchResponse matchResponseDummy;
        private RoundResponse roundResponseDummy;

        @BeforeEach
        void setUp() {
                // Dummy de MatchResponse
                matchResponseDummy = new MatchResponse();
                matchResponseDummy.setId(1L);
                matchResponseDummy.setJornadaId(10L);
                matchResponseDummy.setJornadaNombre("Jornada 1");
                matchResponseDummy.setFecha(LocalDateTime.now().plusDays(3));
                matchResponseDummy.setEquipoLocalId(100L);
                matchResponseDummy.setEquipoLocalNombre("Equipo A");
                matchResponseDummy.setEquipoVisitanteId(200L);
                matchResponseDummy.setEquipoVisitanteNombre("Equipo B");
                matchResponseDummy.setEstado("POR_JUGARSE");

                // Dummy de RoundResponse
                roundResponseDummy = new RoundResponse();
                roundResponseDummy.setId(10L);
                roundResponseDummy.setNombre("Jornada 1");
                roundResponseDummy.setEstado("PROGRAMADA");
        }

        // =======================================================================
        // GET /matches — Listar partidos
        // =======================================================================

        @Test
        @DisplayName("GET /matches → retorna vista 'matches/list' con atributos del modelo")
        void list_deberiaRetornarVistaListConModeloCompleto() throws Exception {
                // --- Arrange ---
                Page<MatchResponse> page = new PageImpl<>(List.of(matchResponseDummy));
                when(matchService.findAll(isNull(), any(Pageable.class))).thenReturn(page);
                when(roundService.findAll(isNull())).thenReturn(List.of(roundResponseDummy));

                // --- Act & Assert ---
                mockMvc.perform(get("/matches"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("matches/list"))
                                .andExpect(model().attributeExists("pageData"))
                                .andExpect(model().attributeExists("rounds"))
                                .andExpect(model().attribute("selectedJornada", (Object) null));

                verify(matchService).findAll(isNull(), any(Pageable.class));
                verify(roundService).findAll(isNull());
        }

        @Test
        @DisplayName("GET /matches?jornadaId=10 → filtra por jornada y retorna vista 'matches/list'")
        void list_conFiltroJornada_deberiaFiltrarCorrectamente() throws Exception {
                // --- Arrange ---
                Page<MatchResponse> page = new PageImpl<>(List.of(matchResponseDummy));
                when(matchService.findAll(eq(10L), any(Pageable.class))).thenReturn(page);
                when(roundService.findAll(isNull())).thenReturn(List.of(roundResponseDummy));

                // --- Act & Assert ---
                mockMvc.perform(get("/matches").param("jornadaId", "10"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("matches/list"))
                                .andExpect(model().attribute("selectedJornada", 10L));

                verify(matchService).findAll(eq(10L), any(Pageable.class));
        }

        // =======================================================================
        // GET /matches/new — Formulario de creación
        // =======================================================================

        @Test
        @DisplayName("GET /matches/new → retorna vista 'matches/form' con atributos vacíos")
        void createForm_deberiaRetornarFormularioVacio() throws Exception {
                // --- Arrange ---
                when(roundService.findAll(isNull())).thenReturn(List.of(roundResponseDummy));

                // --- Act & Assert ---
                mockMvc.perform(get("/matches/new"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("matches/form"))
                                .andExpect(model().attributeExists("matchRequest"))
                                .andExpect(model().attributeExists("rounds"))
                                .andExpect(model().attributeExists("teams"));

                verify(roundService).findAll(isNull());
        }

        // =======================================================================
        // POST /matches — Crear partido
        // =======================================================================

        @Test
        @DisplayName("POST /matches con datos válidos → redirige a /matches con mensaje de éxito")
        void create_conDatosValidos_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                when(matchService.create(any(MatchRequest.class))).thenReturn(matchResponseDummy);

                // --- Act & Assert ---
                mockMvc.perform(post("/matches")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("jornadaId", "10")
                                .param("equipoLocalId", "100")
                                .param("equipoVisitanteId", "200")
                                .param("fecha", "2025-12-01T20:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("success", "Partido creado exitosamente"));

                verify(matchService).create(any(MatchRequest.class));
        }

        @Test
        @DisplayName("POST /matches cuando el servicio lanza excepción → redirige con mensaje de error")
        void create_cuandoServicioLanzaExcepcion_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                when(matchService.create(any(MatchRequest.class)))
                                .thenThrow(new RuntimeException(
                                                "El equipo local ya tiene un partido asignado en esta jornada"));

                // --- Act & Assert ---
                mockMvc.perform(post("/matches")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("jornadaId", "10")
                                .param("equipoLocalId", "100")
                                .param("equipoVisitanteId", "200")
                                .param("fecha", "2025-12-01T20:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("error",
                                                "El equipo local ya tiene un partido asignado en esta jornada"));
        }

        // =======================================================================
        // GET /matches/{id}/edit — Formulario de edición
        // =======================================================================

        @Test
        @DisplayName("GET /matches/1/edit → retorna vista 'matches/form' con datos del partido")
        void editForm_deberiaRetornarFormularioConDatosExistentes() throws Exception {
                // --- Arrange ---
                when(matchService.findById(1L)).thenReturn(matchResponseDummy);
                when(roundService.findAll(isNull())).thenReturn(List.of(roundResponseDummy));
                when(teamService.findAll()).thenReturn(List.of());

                // --- Act & Assert ---
                mockMvc.perform(get("/matches/1/edit"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("matches/form"))
                                .andExpect(model().attributeExists("matchRequest"))
                                .andExpect(model().attribute("matchId", 1L))
                                .andExpect(model().attributeExists("rounds"))
                                .andExpect(model().attributeExists("teams"));

                verify(matchService).findById(1L);
        }

        // =======================================================================
        // POST /matches/{id}/update — Actualizar partido
        // =======================================================================

        @Test
        @DisplayName("POST /matches/1/update con datos válidos → redirige con mensaje de éxito")
        void update_conDatosValidos_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                when(matchService.update(eq(1L), any(MatchRequest.class))).thenReturn(matchResponseDummy);

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/update")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("jornadaId", "10")
                                .param("equipoLocalId", "100")
                                .param("equipoVisitanteId", "200")
                                .param("fecha", "2025-12-01T20:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("success", "Partido actualizado exitosamente"));

                verify(matchService).update(eq(1L), any(MatchRequest.class));
        }

        @Test
        @DisplayName("POST /matches/1/update cuando servicio lanza excepción → redirige con error")
        void update_cuandoServicioLanzaExcepcion_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                when(matchService.update(eq(1L), any(MatchRequest.class)))
                                .thenThrow(new RuntimeException(
                                                "Solo se pueden modificar partidos en estado POR JUGARSE"));

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/update")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("jornadaId", "10")
                                .param("equipoLocalId", "100")
                                .param("equipoVisitanteId", "200")
                                .param("fecha", "2025-12-01T20:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("error",
                                                "Solo se pueden modificar partidos en estado POR JUGARSE"));
        }

        // =======================================================================
        // POST /matches/{id}/start — Iniciar partido
        // =======================================================================

        @Test
        @DisplayName("POST /matches/1/start exitoso → redirige con mensaje de éxito")
        void startMatch_exitoso_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                MatchResponse enJuego = new MatchResponse();
                enJuego.setId(1L);
                enJuego.setEstado("EN_JUEGO");
                when(matchService.startMatch(1L)).thenReturn(enJuego);

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/start"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("success",
                                                "El partido ha comenzado oficialmente. Estado de jornada verificado."));

                verify(matchService).startMatch(1L);
        }

        @Test
        @DisplayName("POST /matches/1/start cuando el partido no está en POR_JUGARSE → redirige con error")
        void startMatch_estadoInvalido_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                when(matchService.startMatch(1L))
                                .thenThrow(new RuntimeException(
                                                "Solo se pueden iniciar partidos en estado POR_JUGARSE"));

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/start"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("error",
                                                "Solo se pueden iniciar partidos en estado POR_JUGARSE"));
        }

        // =======================================================================
        // GET /matches/{id}/finalizar — Formulario de finalización
        // =======================================================================

        @Test
        @DisplayName("GET /matches/1/finalizar → retorna vista 'matches/finalizar' con el partido en el modelo")
        void finalizeForm_deberiaRetornarVistaConPartido() throws Exception {
                // --- Arrange ---
                when(matchService.findById(1L)).thenReturn(matchResponseDummy);

                // --- Act & Assert ---
                mockMvc.perform(get("/matches/1/finalizar"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("matches/finalizar"))
                                .andExpect(model().attributeExists("match"))
                                .andExpect(model().attributeExists("matchResultRequest"));

                verify(matchService).findById(1L);
        }

        // =======================================================================
        // POST /matches/{id}/finalizar — Finalizar partido con resultado
        // =======================================================================

        @Test
        @DisplayName("POST /matches/1/finalizar con resultado válido → redirige con mensaje de éxito")
        void finalize_conResultadoValido_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                MatchResponse finalizado = new MatchResponse();
                finalizado.setId(1L);
                finalizado.setEstado("FINALIZADO");
                finalizado.setPuntosLocal(2);
                finalizado.setPuntosVisitante(1);
                when(matchService.finalize(eq(1L), eq(2), eq(1))).thenReturn(finalizado);

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/finalizar")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("puntosLocal", "2")
                                .param("puntosVisitante", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("success", "Partido finalizado exitosamente"));

                verify(matchService).finalize(1L, 2, 1);
        }

        @Test
        @DisplayName("POST /matches/1/finalizar cuando el partido ya está finalizado → redirige con error")
        void finalize_yaFinalizado_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                when(matchService.finalize(eq(1L), anyInt(), anyInt()))
                                .thenThrow(new RuntimeException("El partido ya esta finalizado"));

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/finalizar")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("puntosLocal", "2")
                                .param("puntosVisitante", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("error", "El partido ya esta finalizado"));
        }

        // =======================================================================
        // POST /matches/{id}/delete — Eliminar partido
        // =======================================================================

        @Test
        @DisplayName("POST /matches/1/delete exitoso → redirige con mensaje de éxito")
        void delete_exitoso_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                doNothing().when(matchService).delete(1L);

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/delete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("success", "Partido eliminado exitosamente"));

                verify(matchService).delete(1L);
        }

        @Test
        @DisplayName("POST /matches/1/delete cuando tiene pronósticos → redirige con error")
        void delete_conPronosticosAsociados_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                doThrow(new RuntimeException("No se puede eliminar el partido porque tiene pronosticos registrados"))
                                .when(matchService).delete(1L);

                // --- Act & Assert ---
                mockMvc.perform(post("/matches/1/delete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/matches"))
                                .andExpect(flash().attribute("error",
                                                "No se puede eliminar el partido porque tiene pronosticos registrados"));
        }
}
