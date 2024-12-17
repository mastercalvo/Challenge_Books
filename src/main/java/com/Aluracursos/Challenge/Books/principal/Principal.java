package com.Aluracursos.Challenge.Books.principal;
import com.Aluracursos.Challenge.Books.model.*;
import com.Aluracursos.Challenge.Books.repository.LibroRepository;
import com.Aluracursos.Challenge.Books.repository.SujetoRepository;
import com.Aluracursos.Challenge.Books.sevice.ConsumoAPI;
import com.Aluracursos.Challenge.Books.sevice.ConvierteDatos;
import com.Aluracursos.Challenge.Books.sevice.ExcepcionLibroYaExisteEnLaBaseDeDatos;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.*;

import static java.util.spi.ToolProvider.findFirst;


public class Principal {

    ConvierteDatos convertidor = new ConvierteDatos();
    ConsumoAPI conexion = new ConsumoAPI();
    Scanner teclado = new Scanner(System.in);

    private LibroRepository repositorio;
    private SujetoRepository repositorioSujeto;

    @PostConstruct
    public void init() {
        int contador = repositorioSujeto.checkUniqueConstraint();
        if (contador == 0) {
            repositorioSujeto.applyUniqueConstraint();
        }
    }


    public Principal(LibroRepository repository, SujetoRepository repository2) {
        this.repositorio = repository;
        this.repositorioSujeto = repository2;
    }

    //para remover un libro desde el autor . Usando un metodo_OK
    public void removeBookFromAuthor(Libro libro, Sujeto autor) {
        autor.getLibros().remove(libro);
        libro.setAutor(null);
        repositorioSujeto.save(autor);
        repositorio.delete(libro);
    }

    //literalura metodos

    @Transactional
    public void guardarLibroYAutorEnLaDB(DatosLibros libroAGuardar) {
        var libro = new Libro(libroAGuardar);
        Optional<Libro> libroBuscar = repositorio.findByTitulo(libroAGuardar.titulo());
        if(libroBuscar.isPresent()){
            throw new ExcepcionLibroYaExisteEnLaBaseDeDatos("el Libro buscado ya existe");
        }
        try {
            Optional<Sujeto> existeAutor = repositorioSujeto.findByNombre(libroAGuardar.autores().get(0).nombre());
            if (existeAutor.isPresent()) {
                Sujeto autorExistente = repositorioSujeto.autorPorNombre(libroAGuardar.autores().get(0).nombre());
                libro.setAutor(autorExistente);
                autorExistente.getLibros().add(libro);
                repositorio.save(libro);
                errorMensaje="libro guardado. Se agrego el libro al autor existente";
            } else {
                Sujeto autorNuevo = new Sujeto(libroAGuardar.autores().get(0));
                libro.setAutor(autorNuevo);
                autorNuevo.addLibro(libro);
                repositorioSujeto.save(autorNuevo);
                repositorio.save(libro);
                errorMensaje="libro y autor nuevo guardado";
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private DatosLibros buscarLibroPorTituloEnLaAPI() {
        String direccionBase = "https://gutendex.com/books/?search=";
        System.out.println("excribe el titulo del libro a buscar");
        var libroABuscar = teclado.nextLine();
        var json = conexion.obtenerDatos(direccionBase + libroABuscar.replace(" ", "+"));
        try {
            DatosApi datosEncontrados = convertidor.obtenerDatos(json, DatosApi.class);

            Optional<DatosLibros> listaLibrosEncontrados = datosEncontrados.resultadoApi().stream()
                    .filter(l -> l.titulo().toUpperCase().contains(libroABuscar.toUpperCase()))
                    .findFirst();

            if (listaLibrosEncontrados.isPresent()) {
                System.out.println("se encontró el libro");
                errorMensaje="se encontro el libro";
                System.out.println("Titulo  :  " + listaLibrosEncontrados.get());

            } else {
                errorMensaje="no se encontro el libro";
                System.out.println("libro no encontrado");
            }
            return listaLibrosEncontrados.get();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void listarLibrosYSuAutorDeLaDBConJPQL() {
        List<Libro> libros = repositorio.BuscarLibrosConSuAutor();
        libros.forEach(System.out::println);

        errorMensaje="se muestran los libros de la base de datos";
    }

    private void listarAutoresDeLaDB(){
        List<Sujeto> lista =repositorioSujeto.listarTodosLosAutoresYSusLibros();
        if(lista.size()!=0){
            lista.forEach(System.out::println);
            errorMensaje="se muestran los autores de la base de datos";
        }else{
            System.out.println("error al buscar los datos");
            errorMensaje="error al buscar los actores";
        }
    }

    private void listarAutoresDeLaDBVivosEnUnAnioIngresado(){
        System.out.println("ingrese el anio para buscar actores vivos");
        var anioIngresado=teclado.nextLine();
        List<Sujeto> lista =repositorioSujeto.listarAutoresQueEstabanVivosEnUnAnio(Integer.valueOf(anioIngresado));
        if(lista.size()!=0){
            lista.forEach(System.out::println);
            errorMensaje="se encontraron los autores buscados";
        }else{
            errorMensaje="no se encontraron autores vivos El anio tiene que ser mayor al de nacimiento y menor al de fallecimiento";
            System.out.println("error al buscar los datos");
        }
    }

    private void listarLibrosPorIdiomaIngresado(){
        System.out.println("ingrese el idioma de los libros a buscar");
        var idiomaBuscar =teclado.nextLine();
        var idiomaBuscarEnum=Idiomas.fromEspañol(idiomaBuscar);
        var libros =repositorio.findBooksByLanguageWithAuthors(idiomaBuscarEnum);
        if(libros.isEmpty()){
            System.out.println("no se encontraron libros de ese idioma");
            errorMensaje="no se encontraron libros del idioma" + idiomaBuscar;
        }else{
            libros.forEach(System.out::println);
            errorMensaje="libros escritos en " +idiomaBuscar + " encontrados";
        }
    }

    private void estadisticasDeLaBaseDeDatos() {
        List<Libro> libros = repositorio.BuscarLibrosConSuAutor();
        DoubleSummaryStatistics est = libros.stream()
                .filter(l->l.getCantidadDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(Libro::getCantidadDeDescargas));

        String texto = """
                ****** ESTADISTICAS DE DESCARGAS ***********
                
                Cantidad media de descargas: %s
                Cantidad máxima de descargas: %s
                Cantidad mínima de descargas: %s
                Cantidad de registros evaluados para calcular las estadisticas: %s
                
                **************************
                """;

        errorMensaje=String.format(texto,String.valueOf(est.getAverage()),String.valueOf(est.getMax()),String.valueOf(est.getMin()),String.valueOf(est.getCount()));;
    }

    private void buscarAutorPorNombre(){
        System.out.println("escribe el nombre del autor que deseas buscar");
        var nombreAutor = teclado.nextLine();
        Optional<Sujeto> autorABuscar = repositorioSujeto.autorPorNombreLista(nombreAutor);
        var autor= autorABuscar.stream()
                .filter(a -> a.getNombre().toLowerCase().contains(nombreAutor.toLowerCase()))
                .findFirst();
        if(autor.isPresent()){
            System.out.println("el autor buscado de ese nombre : " + autor.get());
            errorMensaje="autores con nombre " + nombreAutor;
        } else {
            System.out.println("no se encontraron");
            errorMensaje="no se encontraron autores de ese nombre";
        }
    }



    String errorMensaje = null;

    public void menu() throws JsonProcessingException {
        //aplica la restriccion de unicidad en los autores por si no se hace via OneToMany
        init();

        var opcion = -1;
        while (opcion != 8) {

            var menu = """
                    1 - Buscar libros por título
                    2 - listar libros registrados
                    3 - listar autores registrados
                    4 - listar autores vivos en un determinado año
                    5 - listar libros por idioma
                    6 - extra - estadisticas cantidad de descargas
                    7 - extra - buscar autorer por nombre
                    *
                    8 - SALIR
                    """;
            System.out.println(menu);
            System.out.println("***********");
            System.out.println("aqui veras mensajes nuevos y anteriores : " + errorMensaje);
            System.out.println("***********");

            System.out.println("");
            System.out.println("");
            System.out.println("Hola");
            System.out.println("Ingresar una opcion numerica :");

            try {
                opcion = teclado.nextInt();
            }catch (InputMismatchException e){
                System.out.println("revise datos ingresados");
                opcion = 8 ;
            }
            try {
                teclado.nextLine();
            }catch (InputMismatchException e){
                System.out.println("revise datos ingresados");
                opcion = 8;
            }

            switch (opcion) {
                case 1:
                    var libroAGuardar =buscarLibroPorTituloEnLaAPI();
                    if(libroAGuardar == null){
                        System.out.println("error al obtener los datos");
                    }else {
                        try{
                            guardarLibroYAutorEnLaDB(libroAGuardar);
                        } catch (ExcepcionLibroYaExisteEnLaBaseDeDatos e){
                            errorMensaje=e.toString();
                            System.out.println(e);
                        }
                    }

                    break;
                case 2: ;
                    listarLibrosYSuAutorDeLaDBConJPQL();

                    break;
                case 3:
                    listarAutoresDeLaDB();

                    break;
                case 4:
                    try{
                        listarAutoresDeLaDBVivosEnUnAnioIngresado();
                    }catch (NumberFormatException e){
                        errorMensaje="ingresar un valor correcto";
                        System.out.println("ingresar un valor correcto");
                    }

                    break;
                case 5:
                    listarLibrosPorIdiomaIngresado();
                    break;
                case 6:
                    estadisticasDeLaBaseDeDatos();
                    break;
                case 7:
                    buscarAutorPorNombre();
                    break;
                case 8:
                    System.out.println("cerrando y saliendo de la aplicacion");
                    break;
                default:
                    System.out.println("opción inválida");
            }
        }

    }
}