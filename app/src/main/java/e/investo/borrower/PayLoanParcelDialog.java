package e.investo.borrower;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.common.DateUtils;
import e.investo.connection.Connection;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;
import e.investo.data.PaymentParcelUnion;
import e.investo.login.LoginActivity;

public class PayLoanParcelDialog extends Dialog implements android.view.View.OnClickListener {

    PaymentParcelUnion mPaymentParcelUnion;

    public PayLoanParcelDialog(Context context, PaymentParcelUnion paymentParcelUnion) {
        super(context);

        mPaymentParcelUnion = paymentParcelUnion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_loan_parcel);

        updateFields();

        Button btnPay = (Button) findViewById(R.id.btnPay);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnPay.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void updateFields() {
        TextView txtParcelNumber = findViewById(R.id.txtParcelNumber);
        TextView txtDueDate = findViewById(R.id.txtDueDate);
        TextView txtValueInfo = findViewById(R.id.txtValueInfo);

        PaymentParcel parcel = mPaymentParcelUnion.uniqueParcel;

        txtParcelNumber.setText(String.format("%02d", parcel.number + 1));
        txtDueDate.setText(CommonFormats.DATE_FORMAT.format(parcel.getDueDate()));
        txtValueInfo.setText(CommonFormats.CURRENCY_FORMAT.format(parcel.value));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPay:
                savePayments();
                Toast.makeText(getContext(), getContext().getString(R.string.payment_made), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void savePayments() {

        Date currentDate = DateUtils.getCurrentDate(true);

        for (PaymentParcel paymentParcel : mPaymentParcelUnion.originalParcels) {
            paymentParcel.setPayday(currentDate);

            DatabaseReference databaseReference =
                    Connection.GetDatabaseReference()
                            .child("Parcelas")
                            .child(paymentParcel.paymentData.id)
                            .child("parcels")
                            .child(String.valueOf(paymentParcel.number));

            databaseReference.setValue(paymentParcel);
        }
    }
}
