package e.investo.data;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class SystemInfo {

    public static SystemInfo Instance = new SystemInfo();

    public String LoggedUserName;
    public String LoggedUserID;
    public Bitmap LoggedUserPhoto;

    public void Update(FirebaseUser user, ContentResolver contentResolver)
    {
        LoggedUserName = user.getDisplayName();
        LoggedUserID = user.getUid();
        LoggedUserPhoto = getUserPhoto(user.getPhotoUrl());
    }

    private Bitmap getUserPhoto(Uri photoUrl) {
        return null;
    }

    public void Reset()
    {
        LoggedUserName = null;
        LoggedUserPhoto = null;
    }
}
