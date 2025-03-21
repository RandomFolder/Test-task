package com.example.utils;

import java.time.LocalDateTime;

import com.example.constants.Constants;

public class Logger {
    private static Logger logger = null;

    private Logger() {}

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void error(String message) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        FileSystemHandler.addLineToFile(Constants.PATH_TO_LOG_FILE, currentDateTime + ": " + message);
    }
}
