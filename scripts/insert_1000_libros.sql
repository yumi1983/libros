DELETE FROM libros;

INSERT INTO libros (titulo, autor, precio)
SELECT
    'Libro ' || n,
    'Autor ' || (MOD(n, 50) + 1),
    ROUND(10 + (n * 0.75), 2)
FROM SYSTEM_RANGE(1, 1000) AS r(n);

-- Verificacion rapida
SELECT COUNT(*) AS total_libros FROM libros;

