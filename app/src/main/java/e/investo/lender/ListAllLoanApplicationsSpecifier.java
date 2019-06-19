package e.investo.lender;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import e.investo.ILoanApplicationListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.conection.Connection;
import e.investo.data.LoanApplication;
import e.investo.lender.adapter.LoanApplicationAdapter;

public class ListAllLoanApplicationsSpecifier implements ILoanApplicationListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;

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
                    list.add(loanApplication);
                }

                mListener.OnLoadCompleted(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "ERRO de conexão", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void OnClick(Context context, LoanApplication loanApplication) {
        Intent it = new Intent(context, LoanApplicationDetailActivity.class);
        it.putExtra(LoanApplicationDetailActivity.EXTRA_LOAN_APPLICATION_ITEM, loanApplication);
        context.startActivity(it);
    }
}
