package com.Aluracursos.Challenge.Books.repository;

import com.Aluracursos.Challenge.Books.model.Sujeto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SujetoRepository extends JpaRepository<Sujeto, Long> {
    @Query("SELECT a FROM Sujeto a WHERE SIZE(a.libros) > 0")
    List<Sujeto> listarAutoresQueTenganLibrosEscritos();
    @Query("SELECT a FROM Sujeto a WHERE a.fechaDeFallecimiento > :anioIngresado AND :anioIngresado > a.fechaDeNacimiento")
    List<Sujeto> listaDeAutoresQueEstabanVivosEnUnAnio(int anioIngresado);
    @Query("SELECT a FROM Sujeto a WHERE a.nombre ILIKE %:nombre%")
    Sujeto autorPorNombre(String nombre);
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE autores ADD CONSTRAINT nombre_autor_unico UNIQUE (nombre)", nativeQuery = true)
    void applyUniqueConstraint();
    @Query(value = "SELECT COUNT(*) FROM information_schema.table_constraints WHERE table_name = 'autores' AND constraint_type = 'UNIQUE' AND constraint_name = 'nombre_autor_unico'", nativeQuery = true)
    int checkUniqueConstraint();
    Optional<Sujeto> findByNombre(String nombre);
    @Query("SELECT DISTINCT a FROM Sujeto a LEFT JOIN FETCH a.libros")
    List<Sujeto> listarTodosLosAutoresYSusLibros();
}
