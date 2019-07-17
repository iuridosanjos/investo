package e.investo.borrower;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import e.investo.IGenericListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.borrower.adapter.BorrowerLoanApplicationsAdapter;
import e.investo.borrower.adapter.PaymentParcelAdapter;
import e.investo.common.CommonConversions;
import e.investo.common.ErrorHandler;
import e.investo.common.LoadingSemaphore;
import e.investo.connection.Connection;
import e.investo.data.LoanApplication;
import e.investo.data.LoanData;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;
import e.investo.data.SystemInfo;

public class PaymentParcelsHistorySpecifier implements IGenericListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;
    private LoanData mLoanData;
    private String mEstablishmentName;
    private boolean mIsBorrower;

    public PaymentParcelsHistorySpecifier(LoanData loanData, String establishmentName, boolean isBorrower)
    {
        mLoanData = loanData;
        mEstablishmentName = establishmentName;
        mIsBorrower = isBorrower;
    }

    @Override
    public void OnCreate(final Context context, ViewGroup rootContainer) {
    }

    @Override
    public void SetPrefixMessage(TextView textView, Context context) {
        textView.setText(getTitle(context));
        textView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemListViewBackground));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int pixels = CommonConversions.ConvertDPValueToPixels(context.getResources(), 10);
        llp.setMargins(0, pixels, 0, pixels); // llp.setMargins(left, top, right, bottom);
        textView.setLayoutParams(llp);
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
        // TODO: terá diferenças entre investidor e empreendedor? Fazer aqui.
        return new PaymentParcelAdapter(context, (List<PaymentParcel>)(Object) itemList);
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

                mListener.OnLoadCompleted(paymentData.parcels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(context, databaseError);
            }
        });
    }

    private OnLoadCompletedEventListener createListener()
    {
        return new OnLoadCompletedEventListener() {
            @Override
            public void OnLoadCompleted(Object result) {
                List<LoanApplication> list = null;
                if (result != null && result instanceof List)
                    list = (List<LoanApplication>)result;

                mListener.OnLoadCompleted(list);
            }
        };
    }

    @Override
    public void OnClick(Context context, Object item) {
        // TODO: abrir oq?

        /*Intent it = new Intent(context, LoanApplicationDetailActivity.class);
        it.putExtra(LoanApplicationDetailActivity.EXTRA_LOAN_APPLICATION_ITEM, loanApplication);
        context.startActivity(it);*/
    }
}
