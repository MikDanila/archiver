package com.mikdanila.archiver;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Danila Mikhaylov on 17/02/2021
 */
public class TarArchiveTest {

    private static Archive tarArchive;
    private static final String archiveName = "src/test/resources/tararchive/output_archive";
    private static final int blockSize = 512;
    private final byte[] expectedHeader = {31, -117, 8};
    private static String[] files = {
            "src/test/resources/tararchive/outer_file",
            "src/test/resources/tararchive/empty_dir/",
            "src/test/resources/tararchive/outer_dir/",
            "src/test/resources/tararchive/outer_dir/file",
            "src/test/resources/tararchive/outer_dir/inner_dir/",
            "src/test/resources/tararchive/outer_dir/inner_dir/inner_file",
            "src/test/resources/tararchive/outer_dir/picture.png",
            "src/test/resources/tararchive/outer_dir/inner_empty/"
    };


    @BeforeClass
    public static void setUpAll(){
        // Set System.out to test file
        tarArchive = new TarArchive(blockSize);
        try {
            File archive = new File(archiveName);
            if (!archive.exists() && archive.createNewFile() ||
                    archive.exists() && archive.delete() && archive.createNewFile()){
                System.setOut(new PrintStream(new FileOutputStream(archive)));
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        // Generate archive
        List<String> files = Arrays.asList(
                "src/test/resources/tararchive/outer_file",
                "src/test/resources/tararchive/empty_dir",
                "src/test/resources/tararchive/outer_dir",
                "src/test/resources/tararchive/wrong_dir",
                "src/test/resources/tararchive/wrong_file.txt"
        );
        tarArchive.archive(files);
        // Set System.out to stdout
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    @AfterClass
    public static void afterAll(){
        File archive = new File(archiveName);
        if (archive.exists()){
            archive.delete();
        }
    }

    @Test
    public void shouldHeaderIsCorrect() throws Exception {
        byte[] actualHeader = new byte[3];
        try (FileInputStream inputStream = new FileInputStream(archiveName)){
            inputStream.read(actualHeader);
            Assert.assertArrayEquals("Header test", expectedHeader, actualHeader);
        }

    }

    @Test
    public void shouldArchivedFiles() throws Exception {
        try (FileInputStream inputStream = new FileInputStream(archiveName);
             InputStream compressorInputStream = new GzipCompressorInputStream(inputStream);
             ArchiveInputStream archiveInputStream = new TarArchiveInputStream(compressorInputStream, blockSize)) {

            List<String> entryFiles = new ArrayList<>();
            ArchiveEntry archiveEntry;
            while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
                Assert.assertTrue(archiveEntry.getName() + " corrupted",
                        archiveInputStream.canReadEntryData(archiveEntry));
                entryFiles.add(archiveEntry.getName());
            }

            Assert.assertArrayEquals("All file exists", entryFiles.toArray(), files);
        }
    }

}