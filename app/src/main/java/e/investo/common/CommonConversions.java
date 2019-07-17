package e.investo.common;

import android.content.res.Resources;
import android.util.TypedValue;

import java.util.Calendar;
import java.util.Date;

public class CommonConversions {

    public static int ConvertDPValueToPixels(Resources resources, int value) {
        return (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                resources.getDisplayMetrics()
        );
    }

    public static double round(double value, int decimalPlaces) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static double roundFloor(double value, int decimalPlaces) {
        return Math.floor(value * 100.0) / 100.0;
    }
}
