package com.mikdanila.dearchiver;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SkipShieldingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Danila Mikhaylov on 17/02/2021
 * @version 1.0
 */
@Service
public class TarUnarchive implements Unarchive {

    private final static Logger log = LoggerFactory.getLogger(TarUnarchive.class);

    private final int blockSize;

    /**
     * @param blockSize size of archive block
     */
    public TarUnarchive(@Value("${archiver.blockSize}") int blockSize) {
        this.blockSize = blockSize;
    }

    /**
     *
     */
    @Override
    public void unarchive() {
        try (InputStream skipShieldingInputStream = new SkipShieldingInputStream(System.in);
             InputStream compressorInputStream = new GzipCompressorInputStream(skipShieldingInputStream);
             ArchiveInputStream archiveInputStream =
                     new TarArchiveInputStream(compressorInputStream, blockSize)){
            // If archive has entry, try to unarchive
            ArchiveEntry archiveEntry;
            while ((archiveEntry = archiveInputStream.getNextEntry()) != null){
                if (!archiveInputStream.canReadEntryData(archiveEntry)){
                    log.error("Can`t read " + archiveEntry.getName());
                } else {
                    untar(archiveInputStream, archiveEntry);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @param archiveInputStream input stream {@link ArchiveInputStream}, which use to unarchive files
     * @param archiveEntry input archive entry, which need unarchive
     */
    private void untar(ArchiveInputStream archiveInputStream, ArchiveEntry archiveEntry){
        File inputFile = new File(archiveEntry.getName());
        if (archiveEntry.isDirectory()) {
            untarDirectory(inputFile);
        } else {
            untarFile(archiveInputStream, inputFile);
        }
    }

    /**
     * @param archiveInputStream input stream {@link ArchiveInputStream}, which use to unarchive files
     * @param inputFile input empty file to unarchive
     */
    private void untarFile(ArchiveInputStream archiveInputStream, File inputFile){
        File parent = inputFile.getParentFile();
        // Try to create parent directories if they doesn't exists
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            log.error(inputFile + " directory can`t create");
            return;
        }
        // Try to unarchive entry from archiveInputStream
        try (OutputStream fileOutputStream = new FileOutputStream(inputFile)) {
            IOUtils.copy(archiveInputStream, fileOutputStream);
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    /**
     * @param inputFile input file to create directory
     */
    private void untarDirectory(File inputFile){
        if (!inputFile.isDirectory() && !inputFile.mkdirs()) {
            log.error(inputFile + " directory can`t create");
        }
    }
}
