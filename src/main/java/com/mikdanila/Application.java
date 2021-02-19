package com.mikdanila;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author Danila Mikhaylov on 17/02/2021
 * @version 1.0
 */

@SpringBootApplication
public class Application {

    /**
     * The entry point of application. Spring will invoke run method in CommandLineExecutor.
     * @see com.mikdanila.executor.CommandLineExecutor#run(String...) 
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
