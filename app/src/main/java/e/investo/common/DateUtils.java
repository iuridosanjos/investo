package e.investo.common;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date createDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        return c.getTime();
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
}
