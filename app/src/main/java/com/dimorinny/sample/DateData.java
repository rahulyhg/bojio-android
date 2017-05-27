package com.dimorinny.sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class DateData {

    ArrayList<Date> dateSlots = new ArrayList<Date>();

    public DateData() {
        ;
    }

    // Takes in start date and end date and outputs an arraylist of dates in increments of half hour
    public void createDates(Date startDate, Date endDate) {
        int days = (int)(endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        Date currentDate = startDate;
        for (int i = 0; i < days; i++) {
            for (int j = 0; j < 48; j++) {
                dateSlots.add(currentDate);
                currentDate = DateUtils.addMinutes(currentDate, 30);
            }
        }
    }

    public static void main(String[] args) throws ParseException {
        DateData test = new DateData();
        SimpleDateFormat start = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        SimpleDateFormat end = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        Date startDate = start.parse("20170111T000000Z");
        Date endDate = end.parse("20170118T000000Z");
        test.createDates(startDate, endDate);
        for (int i = 0; i < test.dateSlots.size(); i++) {
            System.out.println(test.dateSlots.get(i));
        }
    }
}
