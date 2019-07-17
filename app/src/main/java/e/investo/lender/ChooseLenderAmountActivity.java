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
import e.investo.common.CommonConversions;
import e.investo.common.CommonFormats;
import e.investo.common.DateUtils;
import e.investo.connection.Connection;
import e.investo.data.LoanData;
import e.investo.data.LoanApplication;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;
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
        LoanData loanData = saveLoanData();

        if (mLoan.loanData == null)
            mLoan.loanData = new ArrayList<>();
        mLoan.loanData.add(loanData);

        Toast.makeText(getBaseContext(), "Empréstimo realizado!", Toast.LENGTH_LONG).show();

        openSelfLoanDataAndFinish();
    }

    private LoanData saveLoanData() {
        LoanData loanData = new LoanData();
        loanData.id = UUID.randomUUID().toString();
        loanData.setIdUser(SystemInfo.Instance.LoggedUserID);
        loanData.setIdApplication(mLoan.getIdAplication());
        loanData.setDataCriacao(currentTime);
        loanData.setValorEmprestimo(getLendAmount());

        DatabaseReference databaseReference = Connection.GetDatabaseReference().child("Investimento");
        databaseReference.child(loanData.id).setValue(loanData);

        savePaymentData(loanData);

        return loanData;
    }

    private void savePaymentData(LoanData loanData)
    {
        PaymentData paymentData = new PaymentData();
        paymentData.id = UUID.randomUUID().toString();
        paymentData.loanAplicationId = loanData.idApplication;
        paymentData.loanDataId = loanData.id;
        paymentData.payerUserId = loanData.idUser;
        paymentData.parcels = new ArrayList<>();

        Date firstDueDate = getFirstParcelDueDate(mLoan.DueDay);
        double parcelValue = CommonConversions.roundFloor(loanData.valorEmprestimo / mLoan.ParcelsAmount, 2);
        double lastParcelValue = CommonConversions.roundFloor(loanData.valorEmprestimo - ((mLoan.ParcelsAmount - 1) * parcelValue), 2);

        for (int number = 0; number < mLoan.ParcelsAmount; number++)
        {
            PaymentParcel parcel = new PaymentParcel();
            parcel.number = number;
            parcel.setDueDate(addMonthTo(firstDueDate, number));
            parcel.setPayday(null);

            if (number == mLoan.ParcelsAmount - 1) // Última parcela deve ter o valor restante
                parcel.value = lastParcelValue;
            else
                parcel.value = parcelValue;

            paymentData.parcels.add(parcel);
        }

        DatabaseReference databaseReference = Connection.GetDatabaseReference().child("Parcelas");
        databaseReference.child(paymentData.id).setValue(paymentData);
    }

    private Date getFirstParcelDueDate(int day)
    {
        if (day <= 0)
            day = 5;

        Date todayDate = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayDate);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        // Limpa todos os valores de hora, pois estamos interessados apenas na data
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Se o dia desejado já passou do dia de hoje, então será considerado 2 meses à frente. Caso contrário, 1 mês.
        if (DateUtils.getDay(todayDate) >= day)
            calendar.add(Calendar.MONTH, 2);
        else
            calendar.add(Calendar.MONTH, 1);

        return calendar.getTime();
    }

    private Date addMonthTo(Date date, int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);

        return calendar.getTime();
    }

    private void openSelfLoanDataAndFinish()
    {
        Intent it = new Intent(getBaseContext(), GenericListActivity.class);
        it.putExtra(GenericListActivity.EXTRA_LIST_SPECIFIER, new SelfLoanDataSpecifier());
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);

        finish();
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
