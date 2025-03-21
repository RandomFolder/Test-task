package com.example;

import com.example.constants.Constants;
import com.example.databases.WorkersTable;
import com.example.records.Worker;
import com.example.utils.FileSystemHandler;
import com.example.utils.RandomDataGenerator;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments were passed");
            return;
        }

        FileSystemHandler.createFolder(Constants.DATABASE_FOLDER);
        FileSystemHandler.createFolder(Constants.LOG_FOLDER);
        FileSystemHandler.deleteFile(Constants.PATH_TO_LOG_FILE);

        int mode;
        try {
            mode = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            System.out.println("First parameter is not a number");
            return;
        }
        WorkersTable db = new WorkersTable();

        switch (mode) {
            case 1:
                db.createDatabase();
                break;
            case 2:
                if (args.length < 4) {
                    System.out.println("Not enough arguments");
                }
                else {
                    Worker worker = new Worker(args[1], args[2], args[3]);
                    db.insertIntoDatabase(worker);
                }
                break;
            case 3:
                db.showUniqueWorkers();
                break;
            case 4:
                db.beginTransaction();
                db.insertMultipleValues(RandomDataGenerator.generateRandomWorkers(1000000, true));
                db.updateRows();
                db.endTransaction();
                break;
            case 5:
                db.selectAllMales(false);
                break;
            case 6:
                db.selectAllMales(true);
                break;
        }

        db.closeConnection();
    }
}