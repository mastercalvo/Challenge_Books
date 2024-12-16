package com.Aluracursos.Challenge.Books.sevice;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
