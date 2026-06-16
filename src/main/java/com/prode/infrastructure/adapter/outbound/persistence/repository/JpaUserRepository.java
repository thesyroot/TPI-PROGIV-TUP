package com.prode.infrastructure.adapter.outbound.persistence.repository;


import com.prode.infrastructure.adapter.outbound.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    
    @Query(value = """
        SELECT
            u.id AS userId,
            u.nombre AS nombre,
            u.apellido AS apellido,
            u.puntos_total AS puntosTotal,
            COALESCE(SUM(CASE WHEN pt.valor = 3 THEN 1 ELSE 0 END), 0) AS plenos,
            COALESCE(SUM(CASE WHEN pt.valor = 1 THEN 1 ELSE 0 END), 0) AS aciertos,
            MIN(pr.fecha_carga)                                          AS minFechaCarga
        FROM Usuario u
        LEFT JOIN Prediccion pr ON pr.id_usuario = u.id AND pr.estado = 'RESUELTO'
        LEFT JOIN Puntos pt     ON pt.id = pr.id_puntos_asignados
        WHERE u.activo = TRUE AND u.rol = 'USER'
        GROUP BY u.id, u.nombre, u.apellido, u.puntos_total
        ORDER BY u.puntos_total DESC,
                 plenos DESC,
                 minFechaCarga ASC NULLS LAST
        """, nativeQuery = true)
    List<Object[]> findRankingGlobalRaw();

    @Query(value = """
        SELECT
            u.id AS userId,
            u.nombre AS nombre,
            u.apellido AS apellido,
            u.puntos_total AS puntosTotal,
            COALESCE(SUM(CASE WHEN pt.valor = 3 THEN 1 ELSE 0 END), 0) AS plenos,
            COALESCE(SUM(CASE WHEN pt.valor = 1 THEN 1 ELSE 0 END), 0) AS aciertos,
            MIN(pr.fecha_carga)                                          AS minFechaCarga
        FROM Usuario u
        LEFT JOIN Prediccion pr ON pr.id_usuario = u.id AND pr.estado = 'RESUELTO'
        LEFT JOIN Puntos pt     ON pt.id = pr.id_puntos_asignados
        WHERE u.activo = TRUE AND u.id IN (:userIds)
        GROUP BY u.id, u.nombre, u.apellido, u.puntos_total
        ORDER BY u.puntos_total DESC,
                 plenos DESC,
                 minFechaCarga ASC NULLS LAST
        """, nativeQuery = true)
    List<Object[]> findRankingByUserIdsRaw(@Param("userIds") List<Long> userIds);
}
