package com.prode.infrastructure.adapter.inbound.web;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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

import com.prode.application.dto.request.PredictionRequest;
import com.prode.application.dto.response.MatchResponse;
import com.prode.application.dto.response.PredictionResponse;
import com.prode.application.dto.response.RoundResponse;
import com.prode.application.service.MatchService;
import com.prode.application.service.PredictionService;
import com.prode.application.service.RoundService;
import com.prode.application.service.TeamService;
import com.prode.domain.enums.EstadoPrediccion;
import com.prode.domain.enums.Tendencia;
import com.prode.infrastructure.config.TestSecurityConfig;

@WebMvcTest(controllers = UserPredictionWebController.class)
@Import(TestSecurityConfig.class)
class UserPredictionWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PredictionService predictionService;

    @MockitoBean
    private MatchService matchService;

    @MockitoBean
    private RoundService roundService;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private com.prode.infrastructure.security.JwtService jwtService;

    @MockitoBean
    private com.prode.infrastructure.security.TokenBlacklistService tokenBlacklistService;

    @MockitoBean
    private com.prode.infrastructure.security.UserDetailsServiceImpl userDetailsService;

    // -----------------------------------------------------------------------
    // Dummies reutilizables
    // -----------------------------------------------------------------------

    private PredictionResponse predictionResponseDummy;
    private MatchResponse matchResponseDummy;
    private RoundResponse roundResponseDummy;

    private static final String USER_EMAIL    = "usuario@test.com";
    private static final Long   USER_ID       = 42L;
    private static final Long   MATCH_ID      = 10L;
    private static final Long   PREDICTION_ID = 99L;

    @BeforeEach
    void setUp() {
        matchResponseDummy = new MatchResponse();
        matchResponseDummy.setId(MATCH_ID);
        matchResponseDummy.setJornadaId(1L);
        matchResponseDummy.setJornadaNombre("Jornada 1");
        matchResponseDummy.setFecha(LocalDateTime.now().plusHours(2));
        matchResponseDummy.setEquipoLocalId(100L);
        matchResponseDummy.setEquipoLocalNombre("Equipo A");
        matchResponseDummy.setEquipoVisitanteId(200L);
        matchResponseDummy.setEquipoVisitanteNombre("Equipo B");
        matchResponseDummy.setEstado("POR_JUGARSE");

        predictionResponseDummy = new PredictionResponse();
        predictionResponseDummy.setId(PREDICTION_ID);
        predictionResponseDummy.setMatchId(MATCH_ID);
        predictionResponseDummy.setUserId(USER_ID);
        predictionResponseDummy.setLocal("Equipo A");
        predictionResponseDummy.setVisitante("Equipo B");
        predictionResponseDummy.setFechaPartido(LocalDateTime.now().plusHours(2));
        predictionResponseDummy.setPuntosLocal(2);
        predictionResponseDummy.setPuntosVisitante(1);
        predictionResponseDummy.setEstado(EstadoPrediccion.ACTIVO);
        predictionResponseDummy.setTendencia(Tendencia.LOCAL);
        predictionResponseDummy.setBloqueado(false);

        roundResponseDummy = new RoundResponse();
        roundResponseDummy.setId(1L);
        roundResponseDummy.setNombre("Jornada 1");
        roundResponseDummy.setEstado("PROGRAMADA");
    }

    // =======================================================================
    // GET /user/pronosticos  — Listar pronósticos
    // =======================================================================

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos → retorna vista 'user/predictions/list' con atributos del modelo")
    void listPredictions_deberiaRetornarVistaConModeloCompleto() throws Exception {
        // --- Arrange ---
        Page<PredictionResponse> page = new PageImpl<>(List.of(predictionResponseDummy));

        when(predictionService.findUserIdByEmail(USER_EMAIL)).thenReturn(USER_ID);
        when(predictionService.findAllFilteredSecure(
                any(), any(), any(), eq(USER_EMAIL), anyBoolean(), any(Pageable.class)))
                .thenReturn(page);
        when(matchService.findAll((Long) null)).thenReturn(List.of(matchResponseDummy));
        when(roundService.findAll((String) null)).thenReturn(List.of(roundResponseDummy));

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/list"))
                .andExpect(model().attributeExists("predictionsPage"))
                .andExpect(model().attributeExists("upcomingMatches"))
                .andExpect(model().attributeExists("matches"))
                .andExpect(model().attributeExists("rounds"))
                .andExpect(model().attribute("soloMios", false))
                .andExpect(model().attribute("pageTitle", "Pronosticos"))
                .andExpect(model().attribute("currentUserId", USER_ID));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos?soloMios=true → filtra por usuario actual")
    void listPredictions_soloMios_deberiaFiltrarPorUsuarioActual() throws Exception {
        // --- Arrange ---
        Page<PredictionResponse> page = new PageImpl<>(List.of(predictionResponseDummy));

        when(predictionService.findUserIdByEmail(USER_EMAIL)).thenReturn(USER_ID);
        when(predictionService.findAllFilteredSecure(
                any(), any(), any(), eq(USER_EMAIL), anyBoolean(), any(Pageable.class)))
                .thenReturn(page);
        when(matchService.findAll((Long) null)).thenReturn(List.of(matchResponseDummy));
        when(roundService.findAll((String) null)).thenReturn(List.of(roundResponseDummy));

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos").param("soloMios", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/list"))
                .andExpect(model().attribute("soloMios", true));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos?matchId=10&jornada=1 → filtra por partido y jornada")
    void listPredictions_conFiltros_deberiaAplicarlosCorrecto() throws Exception {
        // --- Arrange ---
        Page<PredictionResponse> page = new PageImpl<>(List.of(predictionResponseDummy));

        when(predictionService.findUserIdByEmail(USER_EMAIL)).thenReturn(USER_ID);
        when(predictionService.findAllFilteredSecure(
                any(), any(), any(), eq(USER_EMAIL), anyBoolean(), any(Pageable.class)))
                .thenReturn(page);
        when(matchService.findAll((Long) null)).thenReturn(List.of(matchResponseDummy));
        when(roundService.findAll((String) null)).thenReturn(List.of(roundResponseDummy));

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos")
                        .param("matchId", "10")
                        .param("jornada", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/list"))
                .andExpect(model().attribute("selectedMatchId", MATCH_ID))
                .andExpect(model().attribute("selectedJornada", 1L));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "ADMIN")
    @DisplayName("GET /user/pronosticos siendo ADMIN → isAdmin=true no oculta pronósticos ajenos")
    void listPredictions_siendoAdmin_deberiaUsarFlagAdmin() throws Exception {
        // --- Arrange ---
        Page<PredictionResponse> page = new PageImpl<>(List.of(predictionResponseDummy));

        when(predictionService.findUserIdByEmail(USER_EMAIL)).thenReturn(USER_ID);
        when(predictionService.findAllFilteredSecure(
                any(), any(), any(), eq(USER_EMAIL), anyBoolean(), any(Pageable.class)))
                .thenReturn(page);
        when(matchService.findAll((Long) null)).thenReturn(List.of(matchResponseDummy));
        when(roundService.findAll((String) null)).thenReturn(List.of(roundResponseDummy));

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/list"));
    }

    // =======================================================================
    // GET /user/pronosticos/nuevo  — Formulario de nuevo pronóstico
    // =======================================================================

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos/nuevo → retorna vista 'user/predictions/form' sin partido")
    void newPredictionForm_sinMatchId_deberiaRetornarFormularioVacio() throws Exception {
        // --- Arrange --- (no requiere stub)

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/form"))
                .andExpect(model().attributeExists("predictionRequest"))
                .andExpect(model().attribute("pageTitle", "Nuevo Pronostico"));

        verify(matchService, never()).findById(anyLong());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos/nuevo?matchId=10 → retorna formulario con partido precargado")
    void newPredictionForm_conMatchId_deberiaCargarPartido() throws Exception {
        // --- Arrange ---
        when(matchService.findById(MATCH_ID)).thenReturn(matchResponseDummy);

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos/nuevo").param("matchId", MATCH_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/form"))
                .andExpect(model().attribute("matchId", MATCH_ID))
                .andExpect(model().attributeExists("match"));

        verify(matchService).findById(MATCH_ID);
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos/nuevo?matchId=999 cuando el partido no existe → modelo con match null")
    void newPredictionForm_conMatchIdInexistente_deberiaDejarMatchNull() throws Exception {
        // --- Arrange ---
        when(matchService.findById(999L))
                .thenThrow(new RuntimeException("Partido no encontrado con id: 999"));

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos/nuevo").param("matchId", "999"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/form"))
                .andExpect(model().attribute("match", (Object) null));
    }

    // =======================================================================
    // POST /user/pronosticos  — Crear pronóstico
    // =======================================================================

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("POST /user/pronosticos con datos válidos → redirige con mensaje de éxito")
    void createPrediction_conDatosValidos_deberiaRedirigirConExito() throws Exception {
        // --- Arrange ---
        when(predictionService.create(any(PredictionRequest.class), eq(USER_EMAIL)))
                .thenReturn(predictionResponseDummy);

        // --- Act & Assert ---
        mockMvc.perform(post("/user/pronosticos")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("matchId", MATCH_ID.toString())
                        .param("puntosLocal", "2")
                        .param("puntosVisitante", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("success", "Pronostico guardado exitosamente"));

        verify(predictionService).create(any(PredictionRequest.class), eq(USER_EMAIL));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("POST /user/pronosticos cuando el partido ya comenzó → redirige con error")
    void createPrediction_partidoYaComenzado_deberiaRedirigirConError() throws Exception {
        // --- Arrange ---
        when(predictionService.create(any(PredictionRequest.class), eq(USER_EMAIL)))
                .thenThrow(new RuntimeException(
                        "No se puede pronosticar un partido que comienza en menos de 30 minutos"));

        // --- Act & Assert ---
        mockMvc.perform(post("/user/pronosticos")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("matchId", MATCH_ID.toString())
                        .param("puntosLocal", "1")
                        .param("puntosVisitante", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("error",
                        "No se puede pronosticar un partido que comienza en menos de 30 minutos"));
    }

    // =======================================================================
    // GET /user/pronosticos/{id}/editar  — Formulario de edición
    // =======================================================================

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos/99/editar del usuario dueño → retorna 'user/predictions/form'")
    void editPredictionForm_propietario_deberiaRetornarFormulario() throws Exception {
        // --- Arrange ---
        when(predictionService.findById(PREDICTION_ID)).thenReturn(predictionResponseDummy);
        when(predictionService.findUserIdByEmail(USER_EMAIL)).thenReturn(USER_ID);
        when(matchService.findById(MATCH_ID)).thenReturn(matchResponseDummy);

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos/" + PREDICTION_ID + "/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/predictions/form"))
                .andExpect(model().attributeExists("predictionRequest"))
                .andExpect(model().attribute("predictionId", PREDICTION_ID))
                .andExpect(model().attributeExists("match"))
                .andExpect(model().attribute("pageTitle", "Editar Pronostico"));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos/99/editar de otro usuario → redirige sin mostrar formulario")
    void editPredictionForm_otroUsuario_deberiaRedirigir() throws Exception {
        // --- Arrange ---
        PredictionResponse ajena = new PredictionResponse();
        ajena.setId(PREDICTION_ID);
        ajena.setMatchId(MATCH_ID);
        ajena.setUserId(999L);
        ajena.setFechaPartido(LocalDateTime.now().plusHours(2));

        when(predictionService.findById(PREDICTION_ID)).thenReturn(ajena);
        when(predictionService.findUserIdByEmail(USER_EMAIL)).thenReturn(USER_ID);

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos/" + PREDICTION_ID + "/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"));

        verify(matchService, never()).findById(anyLong());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/pronosticos/99/editar cuando el partido está bloqueado → redirige con error")
    void editPredictionForm_bloqueado_deberiaRedirigirConError() throws Exception {
        // --- Arrange ---
        PredictionResponse bloqueada = new PredictionResponse();
        bloqueada.setId(PREDICTION_ID);
        bloqueada.setMatchId(MATCH_ID);
        bloqueada.setUserId(USER_ID);
        bloqueada.setFechaPartido(LocalDateTime.now().minusMinutes(10));

        when(predictionService.findById(PREDICTION_ID)).thenReturn(bloqueada);
        when(predictionService.findUserIdByEmail(USER_EMAIL)).thenReturn(USER_ID);

        // --- Act & Assert ---
        mockMvc.perform(get("/user/pronosticos/" + PREDICTION_ID + "/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("error", "Ya no puedes editar este pronóstico."));
    }

    // =======================================================================
    // POST /user/pronosticos/{id}/actualizar  — Actualizar pronóstico
    // =======================================================================

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("POST /user/pronosticos/99/actualizar con datos válidos → redirige con éxito")
    void updatePrediction_conDatosValidos_deberiaRedirigirConExito() throws Exception {
        // --- Arrange ---
        when(predictionService.update(eq(PREDICTION_ID), any(PredictionRequest.class), eq(USER_EMAIL)))
                .thenReturn(predictionResponseDummy);

        // --- Act & Assert ---
        mockMvc.perform(post("/user/pronosticos/" + PREDICTION_ID + "/actualizar")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("matchId", MATCH_ID.toString())
                        .param("puntosLocal", "3")
                        .param("puntosVisitante", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("success", "Pronostico actualizado exitosamente"));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("POST /user/pronosticos/99/actualizar intentando modificar uno ajeno → redirige con error")
    void updatePrediction_pronosticoAjeno_deberiaRedirigirConError() throws Exception {
        // --- Arrange ---
        when(predictionService.update(eq(PREDICTION_ID), any(PredictionRequest.class), eq(USER_EMAIL)))
                .thenThrow(new RuntimeException("No puedes modificar un pronostico de otro usuario"));

        // --- Act & Assert ---
        mockMvc.perform(post("/user/pronosticos/" + PREDICTION_ID + "/actualizar")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("matchId", MATCH_ID.toString())
                        .param("puntosLocal", "1")
                        .param("puntosVisitante", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("error",
                        "No puedes modificar un pronostico de otro usuario"));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("POST /user/pronosticos/99/actualizar cuando ya está resuelto → redirige con error")
    void updatePrediction_yaResuelto_deberiaRedirigirConError() throws Exception {
        // --- Arrange ---
        when(predictionService.update(eq(PREDICTION_ID), any(PredictionRequest.class), eq(USER_EMAIL)))
                .thenThrow(new RuntimeException("No se puede modificar un pronostico ya resuelto"));

        // --- Act & Assert ---
        mockMvc.perform(post("/user/pronosticos/" + PREDICTION_ID + "/actualizar")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("matchId", MATCH_ID.toString())
                        .param("puntosLocal", "2")
                        .param("puntosVisitante", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("error",
                        "No se puede modificar un pronostico ya resuelto"));
    }

    // =======================================================================
    // POST /user/pronosticos/{id}/cancelar  — Eliminar pronóstico
    // =======================================================================

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("POST /user/pronosticos/99/cancelar exitoso → redirige con mensaje de éxito")
    void deletePrediction_exitoso_deberiaRedirigirConExito() throws Exception {
        // --- Arrange ---
        doNothing().when(predictionService).delete(PREDICTION_ID, USER_EMAIL);

        // --- Act & Assert ---
        mockMvc.perform(post("/user/pronosticos/" + PREDICTION_ID + "/cancelar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("success", "Pronostico cancelado exitosamente"));

        verify(predictionService).delete(PREDICTION_ID, USER_EMAIL);
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("POST /user/pronosticos/99/cancelar intentando cancelar uno ajeno → redirige con error")
    void deletePrediction_pronosticoAjeno_deberiaRedirigirConError() throws Exception {
        // --- Arrange ---
        doThrow(new RuntimeException("No puedes eliminar un pronostico de otro usuario"))
                .when(predictionService).delete(PREDICTION_ID, USER_EMAIL);

        // --- Act & Assert ---
        mockMvc.perform(post("/user/pronosticos/" + PREDICTION_ID + "/cancelar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/pronosticos"))
                .andExpect(flash().attribute("error",
                        "No puedes eliminar un pronostico de otro usuario"));
    }

    // =======================================================================
    // GET /user/equipo/{id}  — Detalle de equipo
    // =======================================================================

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    @DisplayName("GET /user/equipo/100 → retorna vista 'user/teams/detail' con el equipo en el modelo")
    void teamDetail_deberiaRetornarVistaConEquipo() throws Exception {
        // --- Arrange ---
        com.prode.application.dto.response.TeamResponse teamResponse =
                new com.prode.application.dto.response.TeamResponse();
        teamResponse.setId(100L);
        teamResponse.setNombre("Equipo A");

        when(teamService.findById(100L)).thenReturn(teamResponse);

        // --- Act & Assert ---
        mockMvc.perform(get("/user/equipo/100"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/teams/detail"))
                .andExpect(model().attributeExists("team"))
                .andExpect(model().attribute("pageTitle", "Detalle de Equipo"));

        verify(teamService).findById(100L);
    }
}