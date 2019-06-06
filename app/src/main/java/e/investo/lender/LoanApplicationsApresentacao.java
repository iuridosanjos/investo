package e.investo.lender;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import e.investo.R;
import e.investo.data.LoanApplication;
import e.investo.data.User;
import e.investo.lender.adapter.LoanApplicationAdapter;

public class LoanApplicationsApresentacao extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apresentacao);
    }

}
