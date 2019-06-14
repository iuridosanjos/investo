package e.investo.lender;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.data.DataMocks;
import e.investo.data.LoanApplication;
import e.investo.data.PaymentInfo;

public class ChooseLenderAmountActivity extends AppCompatActivity {

    public static final String EXTRA_LOAN_APPLICATION_ITEM = "LoanApplication";

    TextView txtLoanAmount;
    LoanApplication mLoan;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lender_amount);

        mLoan = (LoanApplication) getIntent().getSerializableExtra(EXTRA_LOAN_APPLICATION_ITEM);
        txtLoanAmount = findViewById(R.id.txtAmount);

        TextView txtMaxLoanAmount = findViewById(R.id.txtMaxAmount);
        txtMaxLoanAmount.setText(CommonFormats.CURRENCY_FORMAT.format(mLoan.RequestedValue));

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar.setMax((int)(mLoan.RequestedValue * 100));

        int progress = seekBar.getProgress();
        updateLendAmount(progress);

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

    private void updateLendAmount(int progress)
    {
        double value = (double)progress / 100;
        txtLoanAmount.setText(CommonFormats.CURRENCY_FORMAT.format(value));
    }
    private double getLendAmount()
    {
        return (double)seekBar.getProgress() / 100;
    }

    public void onLendMoneyClick(View view)
    {
        mLoan.PaymentInfo = new PaymentInfo();
        mLoan.PaymentInfo.TotalValue = getLendAmount();
        mLoan.PaymentInfo.ParcelsAlreadyPayed = 0;
        mLoan.PaymentInfo.ParcelsCount = mLoan.ParcelsAmount;
        mLoan.PaymentInfo.NextDueDate = Calendar.getInstance().getTime();
        mLoan.PaymentInfo.NextParcelNumber = 1;
        mLoan.PaymentInfo.NextParcelValue = mLoan.PaymentInfo.TotalValue / mLoan.PaymentInfo.ParcelsCount;

        DataMocks.AddUserLoanApplication(mLoan);

        Toast.makeText(getBaseContext(), "Empréstimo realizado!", Toast.LENGTH_LONG);

        Intent it = new Intent(getBaseContext(), SelfLoanApplicationsListActivity.class);
        startActivity(it);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            updateLendAmount(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };
}
