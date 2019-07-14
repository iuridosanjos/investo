package e.investo.lender;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
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
import e.investo.borrower.BorrowerLoanApplicationsSpecifier;
import e.investo.borrower.PaymentParcelsHistorySpecifier;
import e.investo.common.CommonConversions;
import e.investo.common.ErrorHandler;
import e.investo.common.LoadingSemaphore;
import e.investo.connection.Connection;
import e.investo.data.LoanData;
import e.investo.data.LoanApplication;
import e.investo.data.SystemInfo;
import e.investo.lender.adapter.SelfLoanApplicationAdapter;

public class SelfLoanDataSpecifier implements IGenericListSpecifier, Serializable {

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
    public BaseAdapter GetAdapter(Context context, List<Object> itemList) {
        return new SelfLoanApplicationAdapter(context, (List<LoanApplication>)(Object)itemList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void LoadDataAsync(final Context context) {
        String userId = SystemInfo.Instance.LoggedUserID;

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        Query query = databaseReference.child("Investimento").orderByChild("idUser").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<LoanData> list = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    LoanData data = objSnapshot.getValue(LoanData.class);
                    list.add(data);
                }

                LoadingSemaphore loadingSemaphore = new LoadingSemaphore(list.size(), createListener());

                loadLoanApplicationsAsync(context, list, loadingSemaphore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(context, databaseError);
            }
        });
    }

    private void loadLoanApplicationsAsync(final Context context, final List<LoanData> loanDataList, final LoadingSemaphore loadingSemaphore) {
        if (loanDataList == null || loanDataList.size() == 0)
            return;

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        for (final LoanData loanData : loanDataList) {
            Query query = databaseReference.child("Aplicacoes").child(loanData.idApplication);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LoanApplication loan = dataSnapshot.getValue(LoanApplication.class);
                    if (loan != null) {
                        loan.loanData = new ArrayList<>();
                        loan.loanData.add(loanData);

                        // Registra o resultado antes de registrar ao semáforo que o dado foi carregado. Queremos que o resultado seja uma List<LoanApplication>.
                        synchronized (loadingSemaphore)
                        {
                            List<LoanApplication> list = (List<LoanApplication>)loadingSemaphore.result;
                            if (list == null)
                                loadingSemaphore.result = list = new ArrayList<>();
                            list.add(loan);
                        }

                        loadingSemaphore.registerLoaded();
                    }
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
        it.putExtra(GenericListActivity.EXTRA_LIST_SPECIFIER, new PaymentParcelsHistorySpecifier(loanApplication.EstablishmentName, false));
        context.startActivity(it);
    }
}
