package e.investo.lender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
        TextView textEstablishmentType = findViewById(R.id.txtEstablishmentType);
        TextView textAddress = (TextView) findViewById(R.id.txtAddress);
        TextView textOwner = (TextView) findViewById(R.id.txtOwner);
        TextView textRequestedValue = (TextView) findViewById(R.id.txtRequestedValue);
        TextView textParcelsInfo = findViewById(R.id.txtParcelsInfo);
        TextView textMonthlyInterests = findViewById(R.id.txtMonthlyInterests);

        if (mLoan != null){
            textEstablishmentName.setText(mLoan.EstablishmentName.toUpperCase());
            textEstablishmentType.setText(mLoan.EstablishmentType.toUpperCase());
            textAddress.setText(mLoan.Address);
            textOwner.setText(mLoan.OwnerName);
            textRequestedValue.setText(CommonFormats.CURRENCY_FORMAT.format(mLoan.getRemainingValue()));
            textParcelsInfo.setText(String.format("%sx", mLoan.ParcelsAmount));
            textMonthlyInterests.setText(String.format("%s a.m.", CommonFormats.PERCENTAGE_FORMAT.format(mLoan.MonthlyInterests * 100)));
        }else{
            Toast.makeText(this, "Erro ao carregar o empreendimento", Toast.LENGTH_SHORT).show();
        }

        ImageButton btnGoToMap = findViewById(R.id.btnSeeAddressOnMap);
        btnGoToMap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onGoToMapClick(v);
            }
        });

        Button btnLendMoney = findViewById(R.id.btnLendMoney);
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
