package com.tasks.notes.helpers;

import java.text.SimpleDateFormat;

public class DateHelper {
    public final static SimpleDateFormat ISO8601_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public final static SimpleDateFormat HUMAN_READABLE_DATE_FORMAT =
            new SimpleDateFormat("E, d MMM yyyy");
}
