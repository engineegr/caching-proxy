/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.sakhalin2.caching_proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author likhobabin_im
 */
public class FileLogWrapper {

    private final Logger fileLogger;

    private final File logFile;

    public FileLogWrapper(String loggerTitle, String fileHandlerPath)
            throws IOException {

        this.fileLogger = Logger.getLogger(loggerTitle);
        this.logFile = new File(fileHandlerPath);
        if (this.logFile.exists() && !this.logFile.canWrite()) {
            throw new IllegalStateException("File exists but couldn't be written");
        } else {
            if (!this.logFile.exists() && !this.logFile.createNewFile()) {
                throw new IllegalStateException("Can't create file by path: " + fileHandlerPath);
            }
        }
        var fh = new FileHandler(fileHandlerPath, true);
        fh.setFormatter(new SimpleFormatter());
        this.fileLogger.addHandler(fh);
    }

    public void throwing(String className, String methodName, Exception error) {
        fileLogger.throwing(className, methodName, error);
    }

    public synchronized void out(Exception error, boolean append) throws FileNotFoundException {
        try (var printOutWriter = new PrintWriter(new FileOutputStream(this.logFile, append))) {
            printOutWriter.append("Error details: BEGIN\n");
            printOutWriter.append(String.format("Classname: %s\n", error.getClass().getCanonicalName()));
            printOutWriter.append(String.format("Localize Message: %s\n", error.getLocalizedMessage()));
            printOutWriter.append(String.format("Message: %s\n", error.getMessage()));
            printOutWriter.append("Stacktrace:\n");
            error.printStackTrace(printOutWriter);
            printOutWriter.append("Error details: END\n");
        }
    }

    public synchronized void append(Exception error) throws FileNotFoundException {
        this.out(error, true);
    }

    public Logger getLogger() {
        return fileLogger;
    }

    public static FileLogWrapper create(String loggerTitle, String fileHandlerPath) {
        try {
            return new FileLogWrapper(loggerTitle, fileHandlerPath);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
