package com.dimorinny.sample;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class iCalParser {

    File iCalFile;
    ArrayList<String> raw = new ArrayList<String>();
    ArrayList<eventObject> events = new ArrayList<eventObject>();
    ArrayList<Date> allDates = new ArrayList<Date>();

    // Constructor: Takes in iCal file
    public iCalParser(File myFile) throws FileNotFoundException, ParseException {
        iCalFile = myFile;
        Scanner myScanner = new Scanner(iCalFile);
        while (myScanner.hasNext()) {
            raw.add(myScanner.nextLine());
        }
        myScanner.close();
        splitEvents();
    }

    // Turns the raw input into an arraylist of eventObjects
    public void splitEvents() throws ParseException {
        // Goes through raw data line by line and creates event objects accordingly
        ArrayList<String> event = new ArrayList<String>();
        for (int i = 3; i < raw.size(); i++) {
            String line = raw.get(i);
            if (line.contains("BEGIN:VEVENT")) {
                event.clear();;
            } else if (line.contains("END:VEVENT")) {
                events.add(new eventObject(event));
            } else if (line.contains("URL:") || line.contains("DESCRIPTION:")) {
                if (!raw.get(i + 1).contains(":")) {
                    String newLine = line + raw.get(i + 1).substring(1);
                    event.add(newLine);
                    i++;
                } else {
                    event.add(line);
                }
            } else {
                event.add(line);
            }
        }
    }

    // Returns arraylist of events
    public ArrayList<eventObject> getEvents() {
        return events;
    }

}
