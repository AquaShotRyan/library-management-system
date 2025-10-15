package org.library;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class LibraryDate {
    private Calendar date;

    public LibraryDate(int year, int month, int day){
        date = new GregorianCalendar(year, month, day);
    }

    public LibraryDate(LibraryDate libraryDate){
        date = (Calendar)libraryDate.getDate().clone();
    }

    public LibraryDate(Calendar calendar){
        date = calendar;
    }

    public void addDays(int days){
        date.add(Calendar.DATE, days);
    }

    public Calendar getDate(){
        return date;
    }

    @Override
    public String toString() {
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH)+1;
        int day = date.get(Calendar.DAY_OF_MONTH);

        String monthStr = (month <= 9) ? "0"+month : ""+month;
        String dayStr = (day <= 9) ? "0"+day : ""+day;

        return String.format("%d-%s-%s", year, monthStr, dayStr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof LibraryDate)) return false;
        LibraryDate libraryDate = (LibraryDate) obj;
        Calendar calendar = libraryDate.getDate();

        boolean equalYear = calendar.get(Calendar.YEAR) == date.get(Calendar.YEAR);
        boolean equalMonth = calendar.get(Calendar.MONTH) == date.get(Calendar.MONTH);
        boolean equalDay = calendar.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH);

        return equalYear && equalMonth && equalDay;
    }
}
