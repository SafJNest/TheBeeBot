package com.safjnest.Utilities;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * This class is used to handle and print dates and playing-time.
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 */
public class DateHandler {

    /**
     * This method is used to print playing-time.
     * @return String with playing-time
     */
    public static String formatDate(OffsetDateTime date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy' 'HH:mm");
		
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(OffsetDateTime.now().toInstant().toEpochMilli() - date.toInstant().toEpochMilli());
        int years = calendar.get(Calendar.YEAR) - 1970;
        int months = calendar.get(Calendar.MONTH);
        int days = calendar.get(Calendar.DAY_OF_MONTH) - 1;

        String finalDate = dtf.format(date) + " (";
        if(years != 0){
            if(years == 1)
                finalDate += years + " year ";
            else
                finalDate += years + " years ";
        }
        if(months != 0){
            if(years != 0)
                finalDate += "and ";
            if(months == 1)
                finalDate += months + " month ";
            else
                finalDate += months + " months ";
        }
        if(years == 0 && months == 0){
            if(days == 1)
                finalDate += days + " day ";
            else
                finalDate += days + " days ";
        }
        finalDate += "ago)";
        return finalDate;
    }
}
