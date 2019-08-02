package e.investo.lender;

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
import java.util.HashMap;
import java.util.Map;

import e.investo.R;
import e.investo.business.PaymentController;
import e.investo.common.CommonConstants;
import e.investo.common.CommonConversions;
import e.investo.common.CommonFormats;
import e.investo.common.DateUtils;
import e.investo.connection.Connection;
import e.investo.data.InterestsAndFineResult;
import e.investo.data.LoanData;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;
import e.investo.data.PaymentParcelUnion;

public class LenderCheckLatePaymentDialog extends Dialog implements View.OnClickListener {

    Activity mOwnerActivity;
    LoanData mLoanData;
    PaymentData mPaymentData;

    public LenderCheckLatePaymentDialog(Context context, Activity ownerActivity, LoanData loanData, PaymentData paymentData) {
        super(context);

        mOwnerActivity = ownerActivity;
        mLoanData = loanData;
        mPaymentData = paymentData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_check_late_payment);

        updateFields();

        Button btnAgree = (Button) findViewById(R.id.btnAgree);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAgree.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void updateFields() {
        TextView txtDescription = findViewById(R.id.txtDescription);

        txtDescription.setText(String.format(getContext().getString(R.string.terms),
                CommonFormats.CURRENCY_FORMAT.format(getRemainingValue(mPaymentData)),
                CommonFormats.PERCENTAGE_FORMAT.format(CommonConstants.LATE_PAYMENT_CHARGE_PERCENTAGE * 100)));
    }

    private double getRemainingValue(PaymentData paymentData) {
        double remainingValue = 0;
        for (PaymentParcel parcel : paymentData.parcels)
            if (parcel.getPayday() == null)
                remainingValue += parcel.value;

        return remainingValue;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAgree:
                saveAutoCharge();
                Toast.makeText(getContext(), getContext().getString(R.string.auto_charge_activated_action), Toast.LENGTH_SHORT).show();
                restartActivity();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void saveAutoCharge() {
        mLoanData.autoChargeActivated = true;
        mLoanData.setAutoChargeActivationDate(DateUtils.getCurrentDate(true));

        DatabaseReference databaseReference =
                Connection.GetDatabaseReference()
                        .child("Investimento")
                        .child(mPaymentData.loanDataId);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("autoChargeActivated", true);
        postValues.put("autoChargeActivationDateLong", mLoanData.autoChargeActivationDateLong);

        databaseReference.updateChildren(postValues);
    }

    private void restartActivity() {
        mOwnerActivity.recreate();
    }
}
