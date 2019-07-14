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
import java.util.List;

import e.investo.GenericListActivity;
import e.investo.IGenericListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.borrower.adapter.BorrowerLoanApplicationsAdapter;
import e.investo.common.CommonConversions;
import e.investo.common.ErrorHandler;
import e.investo.common.LoadingSemaphore;
import e.investo.connection.Connection;
import e.investo.data.LoanData;
import e.investo.data.LoanApplication;
import e.investo.data.SystemInfo;

public class BorrowerLoanApplicationsSpecifier implements IGenericListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;

    @Override
    public void OnCreate(final Context context, ViewGroup rootContainer) {
        FloatingActionButton fab = rootContainer.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateLoanApplicationActivity.class);
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
    public BaseAdapter GetAdapter(Context context, List<Object> itemList) {
        return new BorrowerLoanApplicationsAdapter(context, (List<LoanApplication>)(Object)itemList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void LoadDataAsync(final Context context) {
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

                LoadingSemaphore loadingSemaphore = new LoadingSemaphore(list.size(), createListener());
                // Fixa o resultado, pois os próximos carregamentos serão apenas detalhamentos do que já foi carregado.
                loadingSemaphore.result = list;

                loadDataPayments(context, list, loadingSemaphore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(context, databaseError);
            }
        });
    }

    private void loadDataPayments(final Context context, final List<LoanApplication> loanApplications, final LoadingSemaphore loadingSemaphore) {
        if (loanApplications == null || loanApplications.size() == 0)
            return;

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        for (final LoanApplication loanApplication : loanApplications) {
            loanApplication.loanData = new ArrayList<>();

            Query query = databaseReference.child("Investimento").orderByChild("idApplication").equalTo(loanApplication.getIdAplication());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        LoanData loanData = objSnapshot.getValue(LoanData.class);
                        loanApplication.loanData.add(loanData);
                    }

                    loadingSemaphore.registerLoaded();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ErrorHandler.Handle(context, databaseError);
                }
            });
        }
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
        LoanApplication loanApplication = (LoanApplication)item;

        Intent it = new Intent(context, GenericListActivity.class);
        it.putExtra(GenericListActivity.EXTRA_LIST_SPECIFIER, new PaymentParcelsHistorySpecifier(loanApplication.EstablishmentName, true));
        context.startActivity(it);
    }
}
