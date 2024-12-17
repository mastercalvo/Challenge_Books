package com.Aluracursos.Challenge.Books.sevice;

public class ExcepcionLibroYaExisteEnLaBaseDeDatos extends RuntimeException {
    public ExcepcionLibroYaExisteEnLaBaseDeDatos(String message) {
        super(message);
    }
}
