-- Seed de tipos de puntaje
INSERT INTO Puntos (nombre, valor, activo)
SELECT * FROM (VALUES
    ('Resultado Exacto', 3, TRUE),
    ('Acierto de Tendencia', 1, TRUE),
    ('Sin Acierto', 0, TRUE)
) AS p(nombre, valor, activo)
WHERE NOT EXISTS (SELECT 1 FROM Puntos WHERE nombre = p.nombre);
