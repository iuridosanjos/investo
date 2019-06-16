package e.investo.lender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import e.investo.BaseActivity;
import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.common.CommonIntents;
import e.investo.data.LoanApplication;

public class LoanApplicationDetailActivity extends BaseActivity {

    public static final String EXTRA_LOAN_APPLICATION_ITEM = "LoanApplication";

    private LoanApplication mLoan;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application_detail);

        mLoan = (LoanApplication) getIntent().getSerializableExtra(EXTRA_LOAN_APPLICATION_ITEM);

        updateLayout();
    }

    private void updateLayout()
    {
        TextView textEstablishmentName = (TextView) findViewById(R.id.txtEstablishmentName);
        TextView textAddress = (TextView) findViewById(R.id.txtAddress);
        TextView textOwner = (TextView) findViewById(R.id.txtOwner);
        TextView textRequestedValue = (TextView) findViewById(R.id.txtRequestedValue);

        if (mLoan != null){
            textEstablishmentName.setText(mLoan.EstablishmentName);
            textAddress.setText(mLoan.Address);
            //textOwner.setText(mLoan.Owner.Name);
            textRequestedValue.setText(CommonFormats.CURRENCY_FORMAT.format(mLoan.RequestedValue));
        }else{

            Toast.makeText(this, "Erro ao Carregar o mLoan", Toast.LENGTH_SHORT).show();
        }

        Button btnGoToMap = (Button) findViewById(R.id.btnSeeAddressOnMap);
        btnGoToMap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onGoToMapClick(v);
            }
        });

        Button btnLendMoney = (Button) findViewById(R.id.btnLendMoney);
        btnLendMoney.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onLendMoneyClick(v);
            }
        });
    }


    public void onGoToMapClick(View view)
    {
        Intent it = null;

        if (mLoan.Address != null)
            it = CommonIntents.viewOnMap(mLoan.Address);

        if (it != null)
            startActivity(it);
    }

    public void onLendMoneyClick(View view)
    {
        Intent it = new Intent(getBaseContext(), ChooseLenderAmountActivity.class);
        it.putExtra(ChooseLenderAmountActivity.EXTRA_LOAN_APPLICATION_ITEM, mLoan);
        startActivity(it);
    }
}
