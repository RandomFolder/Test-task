package com.example.records;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import com.example.databases.WorkersTable;

public record Worker(String fullName, String dateOfBirth, String sex) {
    private LocalDate getDateAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(this.dateOfBirth, formatter);
    }

    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = this.getDateAsString();

        Period period = Period.between(birthDate, currentDate);
        
        return period.getYears();
    }

    public void addToTable(WorkersTable table) {
        table.insertIntoDatabase(this);
    }

    public void print() {
        System.out.println(String.format("|\t%s\t|\t%s\t|\t%s\t|\t%d\t|", this.fullName, this.dateOfBirth, this.sex, this.getAge()));
    }
}