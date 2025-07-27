# RPG Scheduler

## Use

You can use this in conjunction with a form submission site like Jotform to organize and assign people who sign up for events.

This is designed primarily for RPG scheduling where you need the following rules

* You have a number of dates you want to run an event
* Folks can indicate one or more events they are available to attend
* You have a cap (like 4 or 8 people) per event
* You want each person to get into 1 event if possible (alerting us if we can't get everyone into 1 event)
* Those who want multiple events can fill in empty seats only after each person is assigned a spot
* Even then, we give everyone who wants multiple a chance at 2 spots before we give someone 3 spots and so on
* You might want a waitlist of those who could attend but won't fit (in case of dropouts later)
* First come first serve applies at all levels

## Setup For JotForm

When you create a JotForm entry you need to do a couple things.

* You need 5 Fields in the CSV file (Please hack/customize CsvImporter.java if you want to make this different).
* First field is always the submission date. You need to change the format of this to be `yyyy-MM-dd HH:mm:ss` via the dropdown in JotForm. This is needed to ensure we do a first come first serve approach when many people submit on the same day.
* Next field is the name of the person. If you use multiple fields for names, just "hide" one in JotForm
* What days you can attend, this can be in any format you like, it should be a number of check boxes which means multiple entries in one 'cell'. The program will split them by new lines.
* Final field is a Yes/No only field if they want to be in multiple sessions. Some people only want 1 session, others want to be in as many as possible..

Download the csv file and put it on your computer.

## Running the progam

Edit the settings.properties file. Change any values. You can affect the number of people allowed in each session, and you can change where the csv file is located. By default the file is named import.csv and is placed in the same directory.
```
MAX_PEOPLE_PER_EVENT=5
CSV_FILE=./import.csv
```

To run the application, make sure you have Java 21+ SDK in your $PATH then run `./gradlew run` and look at the output
