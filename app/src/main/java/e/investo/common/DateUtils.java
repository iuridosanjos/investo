package e.investo.common;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date createDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        return c.getTime();
    }

    public static Date getCurrentDate(boolean withTime) {
        Calendar calendar = Calendar.getInstance();
        if (!withTime)
            clearTime(calendar);

        return calendar.getTime();
    }

    public static void clearTime(Calendar calendar) {
        // Limpa todos os valores de hora, pois estamos interessados apenas na data
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static int daysBetweenDates(Date higherDate, Date lesserDate) {
        long diff = higherDate.getTime() - lesserDate.getTime();
        return (int)(diff / (24 * 60 * 60 * 1000));
    }

    public static Date clearTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        clearTime(calendar);

        return calendar.getTime();
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
