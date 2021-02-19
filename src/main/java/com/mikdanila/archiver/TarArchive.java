package com.mikdanila.archiver;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Danila Mikhaylov on 17/02/2021
 * @version 1.0
 */
@Service
public class TarArchive implements Archive {

    private final static Logger log = LoggerFactory.getLogger(TarArchive.class);

    private final int blockSize;

    /**
     * @param blockSize injection size of archive block
     */
    public TarArchive(@Value("${archiver.blockSize}") int blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * @param inputFilesName list of file name arguments
     */
    @Override
    public void archive(List<String> inputFilesName) {
        try (OutputStream compressorOutputStream = new GzipCompressorOutputStream(System.out);
             ArchiveOutputStream archiveOutputStream =
                     new TarArchiveOutputStream(compressorOutputStream, blockSize)) {
            for (String inputFileName : inputFilesName) {
                tar(archiveOutputStream, inputFileName);
            }
            archiveOutputStream.finish();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * @param archiveOutputStream output stream {@link ArchiveOutputStream}, which use to archive files
     * @param inputFileName input file name from arguments
     */
    private void tar(ArchiveOutputStream archiveOutputStream, String inputFileName) {
        File inputFile = new File(inputFileName);
        // Skip inputFileName if file doesn't exist
        if (!inputFile.exists()) {
            return;
        }
        if (inputFile.isFile()) {
            tarFile(archiveOutputStream, inputFileName, inputFile);
        } else if (inputFile.isDirectory()) {
            tarDirectory(archiveOutputStream, inputFileName, inputFile);
        }
    }

    /**
     * @param archiveOutputStream output stream {@link ArchiveOutputStream}, which use to archive files
     * @param inputFileName input file name, which may have name of parent directory for structural archive
     * @param inputFile input {@link File} to archive
     */
    private void tarFile(ArchiveOutputStream archiveOutputStream, String inputFileName, File inputFile){
        try (InputStream inputStream = new FileInputStream(inputFile)) {
            // Create file in archive
            ArchiveEntry entry = archiveOutputStream.createArchiveEntry(inputFile, inputFileName);
            archiveOutputStream.putArchiveEntry(entry);
            IOUtils.copy(inputStream, archiveOutputStream); // Copy file to output stream and close entry
            archiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @param archiveOutputStream output stream {@link ArchiveOutputStream}, which use to archive files
     * @param inputFileName input file name, which need to concatenate with name of children files, if they exists
     * @param inputFile input file {@link File} to archive
     */
    private void tarDirectory(ArchiveOutputStream archiveOutputStream, String inputFileName, File inputFile){
        try {
            // Create directory in archive
            ArchiveEntry entry = archiveOutputStream.createArchiveEntry(inputFile, inputFileName);
            archiveOutputStream.putArchiveEntry(entry);
            archiveOutputStream.closeArchiveEntry();
            // If directory has another directories or files, recursive call method tarFile()
            for (File file : inputFile.listFiles()) {
                if (file != null){
                    Path filePath = Paths.get(inputFileName, file.getName());
                    tar(archiveOutputStream, filePath.toString());
                }
            }
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

}
