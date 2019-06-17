package e.investo.conection;


import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Connection {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseUser firebaseUser;
    private static DatabaseReference databaseReference;

    private Connection() {

    }

    public  static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            inicializarFirebaseAuth();
        }
        return  firebaseAuth;
    }

    private static void inicializarFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    firebaseUser = user;
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static FirebaseUser getFirebaseUser(){
        return firebaseUser;
    }

    public static DatabaseReference GetDatabaseReference()
    {
        if (databaseReference == null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
            databaseReference = firebaseDatabase.getReference();
        }

        return databaseReference;
    }

    public static  void logOut(){

        if (firebaseAuth != null)
            firebaseAuth .signOut();
    }
}
