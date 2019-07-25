package e.investo.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

import e.investo.R;

public class ErrorHandler {
    public static void Handle(@NonNull Context context, @NonNull DatabaseError databaseError) {
        Toast.makeText(context, context.getResources().getString(R.string.error_database), Toast.LENGTH_SHORT).show();
    }

    public static void Handle(@NonNull Context context, @NonNull Exception exception) {
        String msg = exception.getMessage();
        if (msg == null || msg.length() == 0)
            msg = "Sem informações adicionais";

        Log.e(CommonConstants.LOG_TAG, msg, exception);
        Toast.makeText(context, String.format(context.getResources().getString(R.string.error_default_message), msg), Toast.LENGTH_SHORT).show();
    }

}
