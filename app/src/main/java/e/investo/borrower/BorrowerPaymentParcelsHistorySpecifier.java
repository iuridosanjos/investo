package e.investo.borrower;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import e.investo.IGenericListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.borrower.adapter.BorrowerPaymentParcelAdapter;
import e.investo.business.PaymentController;
import e.investo.common.CommonConversions;
import e.investo.common.ErrorHandler;
import e.investo.connection.Connection;
import e.investo.data.LoanApplication;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;
import e.investo.data.PaymentParcelUnion;

public class BorrowerPaymentParcelsHistorySpecifier implements IGenericListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;
    private LoanApplication mLoanApplication;

    public BorrowerPaymentParcelsHistorySpecifier(LoanApplication loanApplication)
    {
        mLoanApplication = loanApplication;
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
    }

    private String getTitle(Context context)
    {
        return String.format(context.getString(R.string.borrower_payment_data_history_list_prefix), mLoanApplication.EstablishmentName.toUpperCase());
    }

    @Override
    public BaseAdapter GetAdapter(Context context, List<Object> itemList) {
        return new BorrowerPaymentParcelAdapter(context, (List<PaymentParcelUnion>)(Object) itemList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void LoadDataAsync(final Context context) {
        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        Query query = databaseReference.child("Parcelas").orderByChild("loanApplicationId").equalTo(mLoanApplication.idAplication);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PaymentData> paymentDataList = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    PaymentData paymentData = objSnapshot.getValue(PaymentData.class);
                    paymentDataList.add(paymentData);

                    if (paymentData != null && paymentData.parcels != null)
                        Collections.sort(paymentData.parcels);
                }

                // Une as parcelas de todos os investimentos em únicas parcelas para o empreendedor
                List<PaymentParcelUnion> paymentParcels = null;
                try {
                    paymentParcels = PaymentController.unionMultiplePayments(mLoanApplication.ParcelsAmount, paymentDataList);
                } catch (Exception e) {
                    ErrorHandler.Handle(context, e);
                }

                mListener.OnLoadCompleted(paymentParcels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(context, databaseError);
            }
        });
    }

    @Override
    public void OnClick(Context context, Object item) {
        PaymentParcelUnion paymentParcelUnion = (PaymentParcelUnion)item;

        // Apenas se as parcelas não estiverem pagas ainda
        if (paymentParcelUnion.uniqueParcel.getPayday() == null) {
            PayLoanParcelDialog dialog = new PayLoanParcelDialog(context, paymentParcelUnion);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}
