package e.investo.lender;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import e.investo.common.ErrorHandler;
import e.investo.conection.Connection;
import e.investo.data.DataPayment;
import e.investo.data.LoanApplication;
import e.investo.data.SystemInfo;
import e.investo.lender.adapter.LoanApplicationAdapter;

public class ListAllLoanApplicationsSpecifier implements ILoanApplicationListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;

    private List<LoanApplication> loadedLoanApplications = new ArrayList<>();

    @Override
    public void OnCreate(Context context, ViewGroup rootContainer) {

    }

    @Override
    public void SetPrefixMessage(TextView textView, Context context) {
        textView.setText(R.string.loan_applications_list_prefix);
        textView.setTextAppearance(R.style.TextAppearance_AppCompat_Small);
    }

    @Override
    public BaseAdapter GetAdapter(Context context, List<LoanApplication> loanApplicationList) {
        return new LoanApplicationAdapter(context, loanApplicationList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void BeginGetLoanApplications(final Context context) {

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        databaseReference.child("Aplicacoes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<LoanApplication> list = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    LoanApplication loanApplication = objSnapshot.getValue(LoanApplication.class);
                    if (loanApplication.OwnerId == null || !loanApplication.OwnerId.equals(SystemInfo.Instance.LoggedUserID))
                        list.add(loanApplication);
                }

                loadDataPayments(context, list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "ERRO de conexão", Toast.LENGTH_SHORT);
            }
        });
    }

    private void loadDataPayments(final Context context, final List<LoanApplication> loanApplications) {
        if (loanApplications == null || loanApplications.size() == 0) {
            mListener.OnLoadCompleted(null);
            return;
        }

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        for (final LoanApplication loanApplication : loanApplications) {
            loanApplication.DataPayments = new ArrayList<>();

            Query query = databaseReference.child("Investimento").orderByChild("idApplication").equalTo(loanApplication.getIdAplication());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        DataPayment dataPayment = objSnapshot.getValue(DataPayment.class);
                        loanApplication.DataPayments.add(dataPayment);
                    }

                    synchronized (loadedLoanApplications) {
                        loadedLoanApplications.add(loanApplication);

                        if (loadedLoanApplications.size() == loanApplications.size()) {
                            mListener.OnLoadCompleted(loanApplications);
                            loadedLoanApplications = new ArrayList<>();
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
        Intent it = new Intent(context, LoanApplicationDetailActivity.class);
        it.putExtra(LoanApplicationDetailActivity.EXTRA_LOAN_APPLICATION_ITEM, loanApplication);
        context.startActivity(it);
    }
}
