package e.investo.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

import e.investo.R;

public class ErrorHandler {
    public static void Handle(@NonNull Context context, @NonNull DatabaseError databaseError)
    {
        Toast.makeText(context, context.getResources().getString(R.string.error_generic_text), Toast.LENGTH_SHORT).show();
    }
}
