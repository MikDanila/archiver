package com.mikdanila.dearchiver;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Danila Mikhaylov on 17/02/2021
 */
public class TarUnarchiveTest {

    private static Unarchive tarUnarchive;
    private static final String unarchiveName = "src/test/resources/tarunarchive/archived";
    private static String[] files = {
            "empty_dir",
            "outer_file",
            "outer_dir/file",
            "outer_dir/picture.png",
            "outer_dir/inner_empty/",
            "outer_dir/inner_dir/inner_file",
            "outer_dir/inner_dir/",
            "outer_dir",
    };

    @BeforeClass
    public static void setUpAll(){
        // Unarchive file from test archive
        tarUnarchive = new TarUnarchive(512);
        try {
            File archive = new File(unarchiveName);
            if (archive.exists()){
                System.setIn(new FileInputStream(archive));
                tarUnarchive.unarchive();
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterAll(){
        // Delete all unarchived file
        for (String file : files){
            File archive = new File(file);
            if (archive.exists()){
                archive.delete();
            }
        }
    }

    @Test
    public void shouldUnarchiveFiles() throws Exception {
        for (String file : files){
            File archive = new File(file);
            Assert.assertTrue(file + " not unarchive", archive.exists());
        }
    }
}