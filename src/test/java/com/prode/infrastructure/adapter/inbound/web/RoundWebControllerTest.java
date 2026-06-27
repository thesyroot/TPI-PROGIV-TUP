package com.prode.infrastructure.adapter.inbound.web;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.prode.application.dto.request.RoundRequest;
import com.prode.application.dto.response.RoundResponse;
import com.prode.application.service.RoundService;

@WebMvcTest(controllers = RoundWebController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoundWebControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private RoundService roundService;

        // Seguridad
        @MockitoBean
        private com.prode.infrastructure.security.JwtService jwtService;

        @MockitoBean
        private com.prode.infrastructure.security.UserDetailsServiceImpl userDetailsService;

        @MockitoBean
        private com.prode.infrastructure.security.TokenBlacklistService tokenBlacklistService;

        // -----------------------------------------------------------------------
        // Dummies reutilizables (@BeforeEach — sección 5.2 del PDF)
        // -----------------------------------------------------------------------

        private RoundResponse roundResponseDummy;

        private static final Long ROUND_ID = 1L;
        private static final String ROUND_NOMBRE = "Jornada 1";

        @BeforeEach
        void setUp() {
                roundResponseDummy = new RoundResponse();
                roundResponseDummy.setId(ROUND_ID);
                roundResponseDummy.setNombre(ROUND_NOMBRE);
                roundResponseDummy.setInicioJornada(LocalDateTime.now().plusDays(1));
                roundResponseDummy.setFinJornada(LocalDateTime.now().plusDays(3));
                roundResponseDummy.setEstado("PROGRAMADA");
                roundResponseDummy.setCantidadPartidos(0);
        }

        // =======================================================================
        // GET /rounds — Listar jornadas
        // =======================================================================

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GET /rounds → retorna vista 'rounds/list' con atributos del modelo")
        void list_deberiaRetornarVistaListConModeloCompleto() throws Exception {
                // --- Arrange ---
                Page<RoundResponse> page = new PageImpl<>(List.of(roundResponseDummy));
                when(roundService.findAll(isNull(), any(Pageable.class))).thenReturn(page);

                // --- Act & Assert ---
                mockMvc.perform(get("/rounds"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("rounds/list"))
                                .andExpect(model().attributeExists("pageData"));

                verify(roundService).findAll(isNull(), any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GET /rounds con página vacía → retorna vista 'rounds/list' con pageData vacío")
        void list_sinJornadas_deberiaRetornarPaginaVacia() throws Exception {
                // --- Arrange ---
                Page<RoundResponse> pageVacia = new PageImpl<>(List.of());
                when(roundService.findAll(isNull(), any(Pageable.class))).thenReturn(pageVacia);

                // --- Act & Assert ---
                mockMvc.perform(get("/rounds"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("rounds/list"))
                                .andExpect(model().attributeExists("pageData"));

                verify(roundService).findAll(isNull(), any(Pageable.class));
        }

        // =======================================================================
        // GET /rounds/new — Formulario de creación
        // =======================================================================

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GET /rounds/new → retorna vista 'rounds/form' con atributo 'roundRequest' vacío")
        void createForm_deberiaRetornarFormularioVacio() throws Exception {
                // --- Arrange --- (no requiere stub)

                // --- Act & Assert ---
                mockMvc.perform(get("/rounds/new"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("rounds/form"))
                                .andExpect(model().attributeExists("roundRequest"));

                verifyNoInteractions(roundService);
        }

        // =======================================================================
        // POST /rounds — Crear jornada
        // =======================================================================

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds con datos válidos → redirige a /rounds con mensaje de éxito")
        void create_conDatosValidos_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                when(roundService.create(any(RoundRequest.class))).thenReturn(roundResponseDummy);

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("nombre", ROUND_NOMBRE)
                                .param("inicioJornada", "2025-12-01T10:00:00")
                                .param("finJornada", "2025-12-03T22:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("success", "Jornada creada exitosamente"));

                verify(roundService).create(any(RoundRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds con nombre duplicado → redirige a /rounds con mensaje de error")
        void create_conNombreDuplicado_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                when(roundService.create(any(RoundRequest.class)))
                                .thenThrow(new RuntimeException(
                                                "Ya existe una jornada con el nombre: " + ROUND_NOMBRE));

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("nombre", ROUND_NOMBRE)
                                .param("inicioJornada", "2025-12-01T10:00:00")
                                .param("finJornada", "2025-12-03T22:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("error",
                                                "Ya existe una jornada con el nombre: " + ROUND_NOMBRE));
        }

        // =======================================================================
        // GET /rounds/{id}/edit — Formulario de edición
        // =======================================================================

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GET /rounds/1/edit → retorna vista 'rounds/form' con datos de la jornada precargados")
        void editForm_deberiaRetornarFormularioConDatosExistentes() throws Exception {
                // --- Arrange ---
                when(roundService.findById(ROUND_ID)).thenReturn(roundResponseDummy);

                // --- Act & Assert ---
                mockMvc.perform(get("/rounds/" + ROUND_ID + "/edit"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("rounds/form"))
                                .andExpect(model().attributeExists("roundRequest"))
                                .andExpect(model().attribute("roundId", ROUND_ID));

                verify(roundService).findById(ROUND_ID);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GET /rounds/999/edit cuando la jornada no existe → lanza excepción del servicio")
        void editForm_jornadaInexistente_deberiaLanzarExcepcion() throws Exception {
                // --- Arrange ---
                when(roundService.findById(999L))
                                .thenThrow(new RuntimeException("Jornada no encontrada con id: 999"));

                // --- Act & Assert ---
                mockMvc.perform(get("/rounds/999/edit"))
                                .andExpect(status().is5xxServerError());

                verify(roundService).findById(999L);
        }

        // =======================================================================
        // POST /rounds/{id}/update — Actualizar jornada
        // =======================================================================

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds/1/update con datos válidos → redirige a /rounds con mensaje de éxito")
        void update_conDatosValidos_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                when(roundService.update(eq(ROUND_ID), any(RoundRequest.class))).thenReturn(roundResponseDummy);

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds/" + ROUND_ID + "/update")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("nombre", "Jornada 1 Actualizada")
                                .param("inicioJornada", "2025-12-01T10:00:00")
                                .param("finJornada", "2025-12-05T22:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("success", "Jornada actualizada exitosamente"));

                verify(roundService).update(eq(ROUND_ID), any(RoundRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds/1/update con nombre ya tomado por otra jornada → redirige con error")
        void update_conNombreDuplicado_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                when(roundService.update(eq(ROUND_ID), any(RoundRequest.class)))
                                .thenThrow(new RuntimeException("Ya existe una jornada con el nombre: Jornada 2"));

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds/" + ROUND_ID + "/update")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("nombre", "Jornada 2")
                                .param("inicioJornada", "2025-12-01T10:00:00")
                                .param("finJornada", "2025-12-05T22:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("error",
                                                "Ya existe una jornada con el nombre: Jornada 2"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds/999/update cuando la jornada no existe → redirige con error")
        void update_jornadaInexistente_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                when(roundService.update(eq(999L), any(RoundRequest.class)))
                                .thenThrow(new RuntimeException("Jornada no encontrada con id: 999"));

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds/999/update")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("nombre", ROUND_NOMBRE)
                                .param("inicioJornada", "2025-12-01T10:00:00")
                                .param("finJornada", "2025-12-05T22:00:00"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("error", "Jornada no encontrada con id: 999"));
        }

        // =======================================================================
        // POST /rounds/{id}/delete — Eliminar jornada
        // =======================================================================

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds/1/delete exitoso → redirige a /rounds con mensaje de éxito")
        void delete_exitoso_deberiaRedirigirConExito() throws Exception {
                // --- Arrange ---
                doNothing().when(roundService).delete(ROUND_ID);

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds/" + ROUND_ID + "/delete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("success", "Jornada eliminada exitosamente"));

                verify(roundService).delete(ROUND_ID);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds/1/delete cuando la jornada tiene partidos asociados → redirige con error")
        void delete_conPartidosAsociados_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                doThrow(new RuntimeException("No se puede eliminar la jornada porque tiene partidos asociados"))
                                .when(roundService).delete(ROUND_ID);

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds/" + ROUND_ID + "/delete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("error",
                                                "No se puede eliminar la jornada porque tiene partidos asociados"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds/1/delete cuando la jornada no está en estado PROGRAMADA → redirige con error")
        void delete_estadoNoPermitido_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                doThrow(new RuntimeException("Solo se pueden eliminar jornadas en estado PROGRAMADA"))
                                .when(roundService).delete(ROUND_ID);

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds/" + ROUND_ID + "/delete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("error",
                                                "Solo se pueden eliminar jornadas en estado PROGRAMADA"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /rounds/999/delete cuando la jornada no existe → redirige con error")
        void delete_jornadaInexistente_deberiaRedirigirConError() throws Exception {
                // --- Arrange ---
                doThrow(new RuntimeException("Jornada no encontrada con id: 999"))
                                .when(roundService).delete(999L);

                // --- Act & Assert ---
                mockMvc.perform(post("/rounds/999/delete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rounds"))
                                .andExpect(flash().attribute("error", "Jornada no encontrada con id: 999"));
        }
}
