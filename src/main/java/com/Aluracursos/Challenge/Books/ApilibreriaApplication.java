package com.Aluracursos.Challenge.Books;

import com.Aluracursos.Challenge.Books.principal.Principal;
import com.Aluracursos.Challenge.Books.repository.LibroRepository;
import com.Aluracursos.Challenge.Books.repository.SujetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class ApilibreriaApplication implements CommandLineRunner {

	//inyeccionn de dependencias para JPA Hibernate
	@Autowired
	private LibroRepository repository;
	@Autowired
	private SujetoRepository repository2;

	public static void main(String[] args) {
		SpringApplication.run(ApilibreriaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository,repository2);
		principal.menu();

	}
}
