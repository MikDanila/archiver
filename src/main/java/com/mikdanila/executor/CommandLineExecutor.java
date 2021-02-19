package com.mikdanila.executor;

import com.mikdanila.archiver.Archive;
import com.mikdanila.dearchiver.Unarchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Danila Mikhaylov on 17/02/2021
 * @version 1.0
 */
@Component
public class CommandLineExecutor implements CommandLineRunner {

    private final Archive archive;
    private final Unarchive unarchive;

    /**
     * Constructor injection for CommandExecutor class
     *
     * @param archive   inject implementation {@link Archive}
     * @param unarchive inject implementation {@link Unarchive}
     */
    @Autowired
    public CommandLineExecutor(Archive archive, Unarchive unarchive) {
        this.archive = archive;
        this.unarchive = unarchive;
    }

    /**
     * The entry point of CommandLineRunner.
     *
     * @param args the input arguments
     */
    @Override
    public void run(String... args) {
        if (args.length > 0){
            archive.archive(Arrays.asList(args));
        } else {
            unarchive.unarchive();
        }
    }

}
