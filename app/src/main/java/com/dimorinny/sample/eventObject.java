package com.dimorinny.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;

public class eventObject {

    String eventName;
    String type;
    String location;
    String moduleCode;
    Date dateStamp;
    Date dateStart;
    Date dateEnd;
    ArrayList<Date> datesExcluded = new ArrayList<Date>();
    String duration;
    String frequency;


    public eventObject(ArrayList<String> data) throws ParseException {
        setData(data);
    }

    // Helper function for setting data for eventObject
    public void setData(ArrayList<String> data) throws ParseException {
        for (int i = 0; i < data.size(); i++) {
            String dataString = data.get(i);
            // Retrieves Event name and grouping
            if (dataString.contains("DESCRIPTION")) {
                int start = dataString.indexOf("DESCRIPTION:") + ("DESCRIPTION:").length();
                if (dataString.contains("\\n")) {
                    int group = dataString.indexOf("\\n") + ("\\n").length();
                    eventName = dataString.substring(start, group - 2);
                    type = dataString.substring(group);
                } else {
                    eventName = dataString.substring(start);
                }
                // Retrieves Module code
            } else if (dataString.contains("SUMMARY")) {
                int start = dataString.indexOf("SUMMARY") + ("SUMMARY:").length();
                moduleCode = dataString.split(" ")[0].substring(start);
                // Retrieves Event location
            } else if (dataString.contains("LOCATION")) {
                int start = dataString.indexOf("LOCATION") + ("LOCATION:").length();
                location = dataString.substring(start);
                // Retrieves Event Date and Time
            } else if (dataString.contains("DTSTAMP")) {
                int start = dataString.indexOf("DTSTAMP") + ("DTSTAMP:").length();
                dateStamp = convertDT(dataString.substring(start));
                // Retrieves Event Date Start
            } else if (dataString.contains("DTSTART")) {
                int start = dataString.indexOf("DTSTART") + ("DTSTART:").length();
                dateStart = convertDT(dataString.substring(start));
                // Retrieves Event Date End
            } else if (dataString.contains("DTEND")) {
                int start = dataString.indexOf("DTEND") + ("DTEND:").length();
                dateEnd = convertDT(dataString.substring(start));
                // Retrieves Excluded dates
            } else if (dataString.contains("EXDATE")) {
                int start = dataString.indexOf("EXDATE") + ("EXDATE:").length();
                datesExcluded.add(convertDT(dataString.substring(start)));
                // Retrieves duration
            } else if (dataString.contains("DURATION")) {
                int start = dataString.indexOf("DURATION") + ("DURATION:PT").length();
                duration = dataString.substring(start);
                // Retrieves frequency
            } else {
                ;
            }
        }
    }

    // Gets module name
    public String getName() {
        return eventName;
    }
    // Gets grouping for event e.g. Tutorial Group/Lecture
    public String getType() {
        if (type == null) {
            return ("Exam");
        } else {
            return type;
        }
    }
    // Gets location for event
    public String getLocation() {
        if (location == null) {
            return ("Not Found");
        } else {
            return location;
        }
    }
    // Gets module code for event
    public String getModCode() {
        return moduleCode;
    }
    // Gets Date and Time for event
    public Date getDateStamp() {
        return dateStamp;
    }
    // Gets Date start
    public Date getDateStart() {
        return dateStart;
    }
    // Gets Date End
    public Date getDateEnd() {
        return dateEnd;
    }
    // Gets Dates Excluded
    public ArrayList<Date> getExcluded() {
        return datesExcluded;
    }
    // Gets Duration
    public float getDuration() {
        if (duration == null) {
            return (float)(dateEnd.getTime() - dateStart.getTime()) / 3600000;
        } else {
            return Float.parseFloat(duration.substring(0, 1));
        }
    }

    // Compares date to all possible eventObject dates in 17 weeks
    public boolean sameDay(Date date) {
        Calendar dateCheck = Calendar.getInstance();
        Calendar dateCheck2 = Calendar.getInstance();
        dateCheck.setTime(date);
        int month = dateCheck.get(Calendar.MONTH);
        int day = dateCheck.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 17; i++) {
            Date toCheck = DateUtils.addDays(dateStart, 7 * i);
            dateCheck2.setTime(toCheck);
            int month2 = dateCheck2.get(Calendar.MONTH);
            int day2 = dateCheck2.get(Calendar.DAY_OF_MONTH);
            if (!datesExcluded.contains(toCheck) && month == month2 && day == day2) {
                return true;
            } else if (month2 > month) {
                return false;
            }
        }
        return false;
    }

    // Assumes event on the same day as date, checks if any time slot matches time of date
    public boolean getAllTimes(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // Check every half hour according to duration
        for (int i = 0; i < (int)getDuration() * 2; i++) {
            String toCheck = sdf.format(DateUtils.addMinutes(dateStart, 30 * i));
            if (toCheck.equals(sdf.format(date))) {
                return true;
            } else {
                ;
            }
        }
        return false;
    }

    public Date convertDT(String dtCode) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        java.util.Date d = sdf.parse(dtCode);
        return d; // output in your system timezone using toString()
    }

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        File myFile = new File("iCalTest.ics");
        iCalParser myParser = new iCalParser(myFile);
        eventObject event = myParser.events.get(4);
        System.out.println("Event: " + event.getName());
        System.out.println("Time: " + event.getDateStart());
        System.out.println(event.getType());
        SimpleDateFormat end = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        Date testDate = end.parse("20170116T000000Z");
        Calendar dateCheck = Calendar.getInstance();
        dateCheck.setTime(testDate);
        System.out.println(event.sameDay(testDate));
    }
}
