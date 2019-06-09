package e.investo.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import e.investo.R;
import e.investo.lender.LoanApplicationsListActivity;

public class ApresentacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apresentacao);

    }

    public void onClick(View v){
        startActivity(new Intent(this, LoanApplicationsListActivity.class));
    }

}
