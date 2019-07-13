package e.investo.lender;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import e.investo.BaseActivity;
import e.investo.GenericListActivity;
import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.connection.Connection;
import e.investo.data.LoanData;
import e.investo.data.LoanApplication;
import e.investo.data.SystemInfo;

public class ChooseLenderAmountActivity extends BaseActivity {

    public static final String EXTRA_LOAN_APPLICATION_ITEM = "LoanApplication";

    private static final int MINIMUM_VALUE_INCREMENT = 100;

    TextView txtLoanAmount;
    LoanApplication mLoan;
    SeekBar seekBar;
    Date currentTime = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lender_amount);

        mLoan = (LoanApplication) getIntent().getSerializableExtra(EXTRA_LOAN_APPLICATION_ITEM);
        txtLoanAmount = findViewById(R.id.txtAmount);

        double remainingValue = mLoan.getRemainingValue();

        TextView txtMaxLoanAmount = findViewById(R.id.txtMaxAmount);
        txtMaxLoanAmount.setText(CommonFormats.CURRENCY_FORMAT.format(remainingValue));

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar.setMax((int) (remainingValue / MINIMUM_VALUE_INCREMENT));

        int progress = seekBar.getProgress();
        updateLendAmount(progress);

        Button btnLendMoney = (Button) findViewById(R.id.btnLendMoney);
        btnLendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLendMoneyClick(v);
            }
        });
    }

    private void updateLendAmount(int progress) {
        double value = (double) progress * MINIMUM_VALUE_INCREMENT;
        txtLoanAmount.setText(CommonFormats.CURRENCY_FORMAT.format(value));
    }

    private double getLendAmount() {
        return (double) seekBar.getProgress() * MINIMUM_VALUE_INCREMENT;
    }

    public void onLendMoneyClick(View view) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 30);

        /*
        mLoan.PaymentInfo = new PaymentInfo();
        mLoan.PaymentInfo.setIdPayment(UUID.randomUUID().toString());
        mLoan.PaymentInfo.TotalValue = getLendAmount();
        mLoan.PaymentInfo.ParcelsAlreadyPayed = 0;
        mLoan.PaymentInfo.ParcelsCount = mLoan.ParcelsAmount;
        mLoan.PaymentInfo.NextDueDate = c.getTime();
        mLoan.PaymentInfo.NextParcelNumber = 1;
        mLoan.PaymentInfo.NextParcelValue = mLoan.PaymentInfo.TotalValue / mLoan.PaymentInfo.ParcelsCount;
*/

        LoanData loanData = new LoanData();
        loanData.id = UUID.randomUUID().toString();
        loanData.setIdUser(SystemInfo.Instance.LoggedUserID);
        loanData.setIdApplication(mLoan.getIdAplication());
        loanData.setDataCriacao(currentTime);
        loanData.setValorEmprestimo(getLendAmount());

        if (mLoan.loanData == null)
            mLoan.loanData = new ArrayList<>();
        mLoan.loanData.add(loanData);

        DatabaseReference databaseReference = Connection.GetDatabaseReference().child("Investimento");
        databaseReference.child(loanData.id).setValue(loanData);

        Toast.makeText(getBaseContext(), "Empréstimo realizado!", Toast.LENGTH_LONG).show();

        Intent it = new Intent(getBaseContext(), GenericListActivity.class);
        it.putExtra(GenericListActivity.EXTRA_LIST_SPECIFIER, new SelfLoanDataSpecifier());
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
