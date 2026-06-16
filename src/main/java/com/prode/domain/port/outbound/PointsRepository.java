package com.prode.domain.port.outbound;


import com.prode.domain.model.Points;
import java.util.Optional;

public interface PointsRepository {
    Optional<Points> findByValor(Integer valor);
    Points save(Points points);
}
