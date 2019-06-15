package e.investo.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import e.investo.BaseActivity;
import e.investo.R;
import e.investo.borrower.CreateLoanApplication;
import e.investo.conection.Connection;
import e.investo.lender.LoanApplicationsListActivity;

public class SelectProfileViewActivity extends BaseActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView nomeUser;
    private ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile_view);
        inicilizarCmp();
    }

    @Override
    protected void onStart() {
        super.onStart();

       // auth = FirebaseAuth.getInstance();
       // user = auth.getCurrentUser();
        auth = Connection.getFirebaseAuth();
        user = auth.getCurrentUser();

        verificaUser();
    }

    public void onClickLenderProfile(View v){
        startActivity(new Intent(this, LoanApplicationsListActivity.class));
    }

    public void onClickBorrowerProfile(View v){
        startActivity(new Intent(this, CreateLoanApplication.class));
    }

    private void inicilizarCmp() {

        nomeUser = (TextView) findViewById(R.id.nomeUser);
        imgUser = (ImageView) findViewById(R.id.userImg);


    }

    private  void verificaUser(){
        nomeUser.setText(user.getDisplayName());
        //textId.setText("Id : "+ user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(imgUser);
    }

}
