package e.investo.data;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import e.investo.connection.Connection;
import e.investo.login.LoginActivity;

public class SystemInfo {

    public static SystemInfo Instance = new SystemInfo();

    private LoggedUserInfo loggedUserInfo;

    public LoggedUserInfo getLoggedUserInfo(Context context) {
        if (loggedUserInfo == null || loggedUserInfo.ID == null) {
            Toast.makeText(context, "Usuário não está logado", Toast.LENGTH_SHORT).show();
            logOut(context);
        }

        return loggedUserInfo;
    }

    public void Update(FirebaseUser user, ContentResolver contentResolver)
    {
        loggedUserInfo = new LoggedUserInfo();
        loggedUserInfo.Name = user.getDisplayName();
        loggedUserInfo.ID = user.getUid();
        loggedUserInfo.Photo = getUserPhoto(user.getPhotoUrl());
    }

    private Bitmap getUserPhoto(Uri photoUrl) {
        return null;
    }

    public void Reset()
    {
        loggedUserInfo = null;
    }

    public void logOut(Context context) {
        Connection.logOut();
        Reset();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
