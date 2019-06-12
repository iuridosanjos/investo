package e.investo.login;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import e.investo.R;
import e.investo.conection.Conection;

public class Profile extends AppCompatActivity {


    private TextView textEmail, textId;
    private Button btnLogout;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        inicilizarCmp();
        eventClick();
    }

    private void eventClick() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conection.logOut();
                finish();
            }
        });
    }

    private void inicilizarCmp() {

        textEmail = (TextView) findViewById(R.id.emailUser);
        textId = (TextView) findViewById(R.id.idUser);
        btnLogout = (Button) findViewById(R.id.logout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Conection.getFirebaseAuth();
        user = Conection.getFirebaseUser();
        verificaUser();
    }

    private void verificaUser() {
        if(user == null){
            finish();
        }else{
            textEmail.setText("Email : "+ user.getEmail());
            textId.setText("Id : "+ user.getUid());

        }
    }
}
