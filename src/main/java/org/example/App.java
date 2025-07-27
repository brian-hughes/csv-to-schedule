package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bhughes.scheduler.CsvImporter;
import org.bhughes.scheduler.Day;
import org.bhughes.scheduler.EventScheduler;
import org.bhughes.scheduler.Person;

import com.opencsv.exceptions.CsvValidationException;

public class App {
    public static void main(String[] args) throws CsvValidationException, IOException, ParseException {
        Properties p = new Properties();
        p.load(new FileInputStream("./settings.properties"));

        //How many people to schedule per event
        EventScheduler.MAX_PEOPLE_PER_DAY = Integer.parseInt(p.getProperty("MAX_PEOPLE_PER_EVENT", "5"));
        CsvImporter importer = new CsvImporter();
        importer.importCsv(p.getProperty("CSV_FILE", "./import.csv"));
        List<Person> people = importer.getAttendees();
        HashMap<String, Day> dates = importer.getDates();

        System.out.println("Found people" + people );
        System.out.println("Found Dates" + dates.values() );

        //Schedule everyone
        EventScheduler.scheduleEvents(people, dates);
    }
}
