package e.investo.common;

import android.content.Intent;
import android.net.Uri;

import java.net.URLEncoder;

public class CommonIntents {

    public static Intent viewOnMap(String address) {
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("geo:0,0?q=%s",
                        URLEncoder.encode(address))));
    }

    public static Intent viewOnMap(String lat, String lng) {
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("geo:%s,%s", lat, lng)));
    }

    public static Intent call(String phone) {
        return new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
    }
}
