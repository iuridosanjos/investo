package e.investo.common;

import android.content.res.Resources;
import android.util.TypedValue;

public class CommonConversions {

    public static int ConvertDPValueToPixels(Resources resources, int value) {
        return (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                resources.getDisplayMetrics()
        );
    }
}
