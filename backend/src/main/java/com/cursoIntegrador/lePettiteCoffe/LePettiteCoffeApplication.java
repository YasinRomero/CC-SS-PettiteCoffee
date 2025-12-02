package com.cursoIntegrador.lePettiteCoffe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación Spring Boot para Le Pettite Coffe.
 * Esta clase contiene el método main y está marcada con @SpringBootApplication.
 */
@SpringBootApplication
public class LePettiteCoffeApplication {

	/**
     * El método principal que inicia la aplicación Spring Boot.
     *
     * @param args Argumentos de la línea de comandos pasados a la aplicación.
     */
	public static void main(String[] args) {
		SpringApplication.run(LePettiteCoffeApplication.class, args);
	}

}
