package e.investo.common;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class CommonFormats {

    public static DecimalFormat CURRENCY_FORMAT = new DecimalFormat("R$ #,##0.00");

    public static DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#,##0.00'%'");

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

}
