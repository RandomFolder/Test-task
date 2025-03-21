package com.example.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.example.constants.Constants;
import com.example.records.Worker;
import com.github.javafaker.Faker;

public class RandomDataGenerator {
    public static <T> T selectRandomOption(List<T> options) {
        Random random = new Random();
        int randomIndex = random.nextInt(options.size());

        return options.get(randomIndex);
    }

    public static String generateRandomName() {
        String firstName = selectRandomOption(Constants.FIRST_NAMES);
        String secondName = selectRandomOption(Constants.SURNAMES);
        String patronimic = selectRandomOption(Constants.PATRONIMICS);

        return secondName + " " + firstName + " " + patronimic;
    }

    public static Worker generateRandomWorker() {
        String fullName = generateRandomName();
        
        String sex = ((new Random()).nextInt(2) == 0) ? "male" : "female";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Faker faker = new Faker();
        String dateOfBirth = sdf.format(faker.date().birthday());

        return new Worker(fullName, dateOfBirth, sex);
    }

    public static List<Worker> generateRandomWorkers(int count, boolean showStats) {
        Map<String, Integer> gendersCount = new HashMap<>();
        Map<String, Integer> nameFirstLettersCount = new HashMap<>();
        List<Worker> workers = new ArrayList<Worker>();
        
        for (int i = 0; i < count; i++) {
            Worker worker = generateRandomWorker();
            workers.add(worker);

            String nameFirstLetter = String.valueOf(worker.fullName().charAt(0));
            String gender = worker.sex();

            if (!gendersCount.containsKey(gender)) {
                gendersCount.put(gender, 1);
            }
            else {
                gendersCount.put(gender, gendersCount.get(gender) + 1);
            }

            if (!nameFirstLettersCount.containsKey(nameFirstLetter)) {
                nameFirstLettersCount.put(nameFirstLetter, 1);
            }
            else {
                nameFirstLettersCount.put(nameFirstLetter, nameFirstLettersCount.get(nameFirstLetter) + 1);
            }
        }

        if (showStats) {
            System.out.println("Genders percentage:");
            for (String key : gendersCount.keySet()) {
                System.out.println(key + ": " + (gendersCount.get(key) / (float)count * 100) + "%");
            }

            System.out.println("\nFirst letters percentage:");
            for (String key : nameFirstLettersCount.keySet()) {
                System.out.println(key + ": " + (nameFirstLettersCount.get(key) / (float)count * 100) + "%");
            }
        }

        return workers;
    }
}
