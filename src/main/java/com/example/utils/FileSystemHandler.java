package com.example.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSystemHandler {
    public static void createFolder(String path) {
        File folder = new File(path);

        if (folder.mkdir()) {
            System.out.println("Folder has been created");
        }
    }

    public static void deleteFile(String path) {
        File fileForDelete = new File(path);

        fileForDelete.delete();
    }

    public static void addLineToFile(String path, String line) {
        try {
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(line);
            bw.newLine();
            bw.close();
        }
        catch (IOException e) {}    
    }
}
