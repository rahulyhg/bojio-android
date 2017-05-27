package com.dimorinny.sample;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class getFreeSlots {

    ArrayList<Date> dateSlots = new ArrayList<Date>();
    boolean[] freeSlots;
    ArrayList<String> modCodes = new ArrayList<String>();
    ArrayList<eventObject> allEvents = new ArrayList<eventObject>();

    // Takes in list of iCalParsers, start Date and end Date
    public getFreeSlots(ArrayList<iCalParser> listIcals, Date startDate, Date endDate) {
        for (int i = 0; i < listIcals.size(); i++) {
            addEvents(listIcals.get(i).getEvents());
        }
        createDates(startDate, endDate);
        deleteFree();
    }

    // Add non-duplicate events
    public void addEvents(ArrayList<eventObject> listEvents) {
        for (int i = 0; i < listEvents.size(); i++) {
            eventObject currentEvent = listEvents.get(i);
            if (modCodes.contains(currentEvent.getModCode())) {
                // Lectures and Exams clash
                if (currentEvent.getType().contains("Exam")) {
                    ;
                } else {
                    allEvents.add(currentEvent);
                }
            } else {
                allEvents.add(currentEvent);
                modCodes.add(currentEvent.getModCode());
            }
        }
    }

    // Takes in start date and end date and outputs an arraylist of dates in increments of half hour
    public void createDates(Date startDate, Date endDate) {
        int days = (int)(endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        Date currentDate = startDate;
        for (int i = 0; i < days; i++) {
            for (int j = 0; j < 48; j++) {
                Date newDate = DateUtils.addMinutes(currentDate, 30);
                dateSlots.add(newDate);
                currentDate = DateUtils.addMinutes(currentDate, 30);
            }
        }
        freeSlots = new boolean[dateSlots.size()];
        for (int i = 0; i < freeSlots.length; i++) {
            freeSlots[i] = true;
        }
    }

    public void deleteFree() {
        // Iterate through each event and delete slot accordingly
        for (int i = 0; i < allEvents.size(); i++) {
            eventObject event = allEvents.get(i);
            // Iterate through date slots
            for (int j = 0; j < dateSlots.size(); j++) {
                // If not the same day, skip 48 elements to next, **assumes day input starts at 00:00:00**
                if (!event.sameDay(dateSlots.get(j))) {
                    j += 48;
                    // If already taken, continue
                } else if (freeSlots[j] == false) {
                    ;
                    // Same day, if time matches, set free to false
                } else if (event.getAllTimes(dateSlots.get(j))) {
                    freeSlots[j] = false;
                } else {
                    ;
                }
            }
        }
    }

    // Gets the arraylist of free slots
    public ArrayList<Date> FreeSlots() {
        ArrayList<Date> out = new ArrayList<Date>();
        for (int i = 0; i < freeSlots.length; i++) {
            if (freeSlots[i]) {
                out.add(dateSlots.get(i));
            } else {
                ;
            }
        }
        return out;
    }

    public static void main(String[] args) throws FileNotFoundException, ParseException {

        File suyashFile = new File("Suyash.ics");
        File junkaiFile = new File("Junkai.ics");
        File haozheFile = new File("iCalTest.ics");
        iCalParser suyash = new iCalParser(suyashFile);
        iCalParser junkai = new iCalParser(junkaiFile);
        iCalParser haozhe = new iCalParser(haozheFile);
        ArrayList<iCalParser> iCal = new ArrayList<iCalParser>();
        iCal.add(suyash); iCal.add(junkai); iCal.add(haozhe);

        SimpleDateFormat start = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        SimpleDateFormat end = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        Date startDate = start.parse("20170111T000000Z");
        Date endDate = end.parse("20170118T110000Z");

        getFreeSlots getSlots = new getFreeSlots(iCal, startDate, endDate);
        for (int i = 0; i < getSlots.allEvents.size(); i++) {
            eventObject event = getSlots.allEvents.get(i);
            System.out.println(event.getName());
            System.out.println(event.getType());
        }
        ArrayList<Date> free = getSlots.FreeSlots();
        for (int i = 0; i < free.size(); i++) {
            System.out.println(free.get(i));
        }
    }
}
