package e.investo.lender;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import e.investo.IGenericListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.common.CommonConversions;
import e.investo.common.ErrorHandler;
import e.investo.connection.Connection;
import e.investo.data.LoanApplication;
import e.investo.data.LoanData;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;
import e.investo.lender.adapter.LenderPaymentParcelAdapter;

public class LenderPaymentParcelsHistorySpecifier implements IGenericListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;
    private LoanData mLoanData;
    private String mEstablishmentName;
    private boolean mIsBorrower;

    public LenderPaymentParcelsHistorySpecifier(LoanData loanData, String establishmentName)
    {
        mLoanData = loanData;
        mEstablishmentName = establishmentName;
    }

    @Override
    public void OnCreate(final Context context, ViewGroup rootContainer) {
    }

    @Override
    public void SetPrefixMessage(TextView textView, TextView txtPrefixSubMessage, Context context) {
        textView.setText(getTitle(context));
        textView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemListViewBackground));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int pixels = CommonConversions.ConvertDPValueToPixels(context.getResources(), 10);
        llp.setMargins(0, pixels, 0, pixels); // llp.setMargins(left, top, right, bottom);
        textView.setLayoutParams(llp);

        if (mLoanData.autoChargeActivated) {
            txtPrefixSubMessage.setText(getTitle(context));
            txtPrefixSubMessage.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            txtPrefixSubMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            txtPrefixSubMessage.setTextColor(ContextCompat.getColor(context, R.color.colorForegroundInsidePrimaryDark));
            txtPrefixSubMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.parcelPaymentStatus_Alert));
            txtPrefixSubMessage.setTypeface(textView.getTypeface(), Typeface.BOLD);

            llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            pixels = CommonConversions.ConvertDPValueToPixels(context.getResources(), 10);
            llp.setMargins(0, pixels, 0, pixels); // llp.setMargins(left, top, right, bottom);
            txtPrefixSubMessage.setLayoutParams(llp);

        }
    }

    private String getTitle(Context context)
    {
        String text;
        if (mIsBorrower)
            text = context.getString(R.string.borrower_payment_data_history_list_prefix);
        else
            text = context.getString(R.string.lender_payment_data_history_list_prefix);

        return String.format(text, mEstablishmentName.toUpperCase());
    }

    @Override
    public BaseAdapter GetAdapter(Context context, List<Object> itemList) {
        return new LenderPaymentParcelAdapter(context, (List<PaymentParcel>)(Object) itemList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void LoadDataAsync(final Context context) {
        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        Query query = databaseReference.child("Parcelas").orderByChild("loanDataId").equalTo(mLoanData.id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PaymentData paymentData = null;
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    paymentData = objSnapshot.getValue(PaymentData.class);
                    break;
                }

                if (paymentData != null && paymentData.parcels != null)
                    Collections.sort(paymentData.parcels);

                mListener.OnLoadCompleted(paymentData == null ? null : paymentData.parcels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(context, databaseError);
            }
        });
    }

    @Override
    public void OnClick(Context context, Object item) {
        PaymentParcel parcel = (PaymentParcel)item;

        /* Apenas se as parcelas não estiverem pagas ainda
        if (parcel.isLatePayment() && !parcel.autoChargeActivated) {
            LenderCheckLatePaymentDialog dialog = new LenderCheckLatePaymentDialog(context, parcel.paymentData);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }*/
    }
}
