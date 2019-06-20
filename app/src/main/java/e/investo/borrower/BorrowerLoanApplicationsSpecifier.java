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
import e.investo.borrower.adapter.BorrowerLoanApplicationsAdapter;
import e.investo.common.CommonConversions;
import e.investo.conection.Connection;
import e.investo.data.DataPayment;
import e.investo.data.LoanApplication;
import e.investo.data.SystemInfo;

public class BorrowerLoanApplicationsSpecifier implements ILoanApplicationListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;

    private List<LoanApplication> loadedLoanApplications = new ArrayList<>();

    @Override
    public void OnCreate(final Context context, ViewGroup rootContainer) {
        FloatingActionButton fab = rootContainer.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateLoanApplication.class);
                context.startActivity(intent);
            }
        });

        fab.show();
    }

    @Override
    public void SetPrefixMessage(TextView textView, Context context) {
        textView.setText(R.string.borrower_loan_applications_list_prefix);
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
        return new BorrowerLoanApplicationsAdapter(context, loanApplicationList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void BeginGetLoanApplications(final Context context) {
        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        Query query = databaseReference.child("Aplicacoes").orderByChild("OwnerId").equalTo(SystemInfo.Instance.LoggedUserID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LoanApplication> list = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    LoanApplication loanApplication = objSnapshot.getValue(LoanApplication.class);
                    list.add(loanApplication);
                }

                loadDataPayments(context, list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, R.string.error_generic_text, Toast.LENGTH_SHORT);
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
                    Toast.makeText(context, R.string.error_generic_text, Toast.LENGTH_SHORT);
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
