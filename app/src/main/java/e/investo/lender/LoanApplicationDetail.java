package e.investo.lender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import e.investo.R;

public class LoanApplicationDetail extends AppCompatActivity {

    public static final String EXTRA_LOAN_APPLICATION_ITEM = "LoanApplication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application_detail);
    }
}
