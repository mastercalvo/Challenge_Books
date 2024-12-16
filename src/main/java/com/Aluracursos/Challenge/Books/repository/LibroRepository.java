package com.Aluracursos.Challenge.Books.repository;

import com.Aluracursos.Challenge.Books.model.Idiomas;
import com.Aluracursos.Challenge.Books.model.Libro;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository {
    List<Libro> findAllByOrderByCantidadDeDescargasDes();
    List<Libro> findByCategoria(Idiomas idiomas);
    Optional<Libro> findByTitulo(String titulo);
    @Query("SELECT l FROM Libro l JOIN FETCH l.autor ")
    List<Libro> BuscarlibroConSuAutor();
    @Query("SELECT l FROM Libro l JOIN FETCH l.autor WHERE l.categoria = :categoria")
    List<Libro> findBooksBylanguageWitAuthors(@Param("categoria") Idiomas categoria);
}
