package e.investo.lender;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import e.investo.ILoanApplicationListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.common.CommonConversions;
import e.investo.common.ErrorHandler;
import e.investo.conection.Connection;
import e.investo.data.DataPayment;
import e.investo.data.LoanApplication;
import e.investo.data.SystemInfo;
import e.investo.lender.adapter.SelfLoanApplicationAdapter;

public class SelfLoanApplicationsSpecifier implements ILoanApplicationListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;

    private List<LoanApplication> loadedLoanApplications = new ArrayList<>();

    @Override
    public void OnCreate(Context context, ViewGroup rootContainer) {

    }

    @Override
    public void SetPrefixMessage(TextView textView, Context context) {
        textView.setText(R.string.self_loan_applications_list_prefix);
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

    @Override
    public BaseAdapter GetAdapter(Context context, List<LoanApplication> loanApplicationList) {
        return new SelfLoanApplicationAdapter(context, loanApplicationList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void BeginGetLoanApplications(final Context context) {
        String userId = SystemInfo.Instance.LoggedUserID;

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        Query query = databaseReference.child("Investimento").orderByChild("idUser").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<DataPayment> list = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    DataPayment data = objSnapshot.getValue(DataPayment.class);
                    list.add(data);
                }

                loadLoanApplications(context, list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(context, databaseError);
            }
        });
    }

    private void loadLoanApplications(final Context context, final List<DataPayment> dataPayments) {
        if (dataPayments == null || dataPayments.size() == 0) {
            mListener.OnLoadCompleted(null);
            return;
        }

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        for (final DataPayment dataPayment : dataPayments) {
            Query query = databaseReference.child("Aplicacoes").child(dataPayment.idApplication);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LoanApplication loan = dataSnapshot.getValue(LoanApplication.class);
                    if (loan != null) {
                        loan.DataPayments = new ArrayList<>();
                        loan.DataPayments.add(dataPayment);

                        synchronized (loadedLoanApplications) {
                            loadedLoanApplications.add(loan);

                            if (loadedLoanApplications.size() == dataPayments.size()) {
                                mListener.OnLoadCompleted(loadedLoanApplications);
                                loadedLoanApplications = new ArrayList<>();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ErrorHandler.Handle(context, databaseError);
                }
            });
        }
    }

    @Override
    public void OnClick(Context context, LoanApplication loanApplication) {
        // TODO: abrir oq?

        /*Intent it = new Intent(context, LoanApplicationDetailActivity.class);
        it.putExtra(LoanApplicationDetailActivity.EXTRA_LOAN_APPLICATION_ITEM, loanApplication);
        context.startActivity(it);*/
    }
}
