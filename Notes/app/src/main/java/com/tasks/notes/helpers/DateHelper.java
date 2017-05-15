package com.tasks.notes.helpers;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateHelper {
    public final static DateTimeFormatter ISO8601_DATE_FORMAT =
            ISODateTimeFormat.dateTime();

    public final static DateTimeFormatter HUMAN_READABLE_DATE_FORMAT =
            DateTimeFormat.forPattern("E, d MMM yyyy");

    public static int compareStrings(String s1, String s2) {
        DateTime d1 = new DateTime(s1);
        DateTime d2 = new DateTime(s2);
        return d1.compareTo(d2);
    }

    public static boolean before(String s1, String s2) {
        return compareStrings(s1, s2) <= 0;
    }
}
