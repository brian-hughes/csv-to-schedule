package org.bhughes.scheduler;

import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvImporter {
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final HashMap<String, Day> days = new HashMap<>();

    private final List<Person> people = new ArrayList<>();

    public void importCsv(String filePath) throws IOException, CsvValidationException, ParseException {
        List<Person> p = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.readNext(); //skip header
            String[] line;

            while ((line = reader.readNext()) != null) {
                if(line[3] == null || line[3].isEmpty()) {
                    line[3] = "no";
                }
                if(line[3].toLowerCase().equals("yes")) {
                    line[3] = "true";
                }
                else {
                    line[3] = "false";
                }

                ArrayList<Day>attendeeDates = new ArrayList<>();
                //add each day to the set to ensure we only have 1 copy of each
                //addionally add that day to the attendee list so we have a refernce to it.
                Arrays.asList(line[2].split("\n")).forEach(dateString -> {
                    if(!days.containsKey(dateString)) {
                        days.put(dateString, new Day(dateString, new ArrayList<>(), new ArrayList<>()));
                    }
                    attendeeDates.add(days.get(dateString));
                });
                p.add(new Person(line[1], attendeeDates, new ArrayList<>(), new ArrayList<>(), format.parse(line[0]) , Boolean.parseBoolean(line[3].toLowerCase())));
            }
        }
        people.addAll(p.reversed());
    }

    public List<Person> getAttendees() {
        return this.people;
    }

    public HashMap<String, Day> getDates() {
        return this.days;
    }
}
