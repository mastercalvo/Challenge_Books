package com.Aluracursos.Challenge.Books.principal;

import com.Aluracursos.Challenge.Books.model.DatosApi;
import com.Aluracursos.Challenge.Books.sevice.ConsumoAPI;
import com.Aluracursos.Challenge.Books.sevice.ConvierteDatos;

import java.util.Scanner;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    public void muestraMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, DatosApi.class);
        System.out.println(datos);
    }


}
