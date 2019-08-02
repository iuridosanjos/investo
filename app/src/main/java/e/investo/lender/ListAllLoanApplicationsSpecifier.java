package e.investo.lender;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Date;
import java.util.List;

import e.investo.IGenericListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.common.DateUtils;
import e.investo.common.ErrorHandler;
import e.investo.common.LoadingSemaphore;
import e.investo.connection.Connection;
import e.investo.data.LoanData;
import e.investo.data.LoanApplication;
import e.investo.data.comparators.LoanApplicationExpirationAndCreationDateComparator;
import e.investo.lender.adapter.ListAllLoanApplicationAdapter;

public class ListAllLoanApplicationsSpecifier implements IGenericListSpecifier, Serializable {

    private OnLoadCompletedEventListener mListener;

    @Override
    public void OnCreate(Context context, ViewGroup rootContainer) {

    }

    @Override
    public void SetPrefixMessage(TextView textView, TextView txtPrefixSubMessage, Context context) {
        textView.setText(R.string.loan_applications_list_prefix);
        textView.setTextAppearance(R.style.TextAppearance_AppCompat_Small);
    }

    @Override
    public BaseAdapter GetAdapter(Context context, List<Object> itemList) {
        return new ListAllLoanApplicationAdapter(context, (List<LoanApplication>)(Object)itemList);
    }

    @Override
    public void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener) {
        mListener = listener;
    }

    @Override
    public void LoadDataAsync(final Context context) {

        DatabaseReference databaseReference = Connection.GetDatabaseReference();

        databaseReference.child("Aplicacoes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<LoanApplication> list = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    LoanApplication loanApplication = objSnapshot.getValue(LoanApplication.class);
                    if (checkLoanApplicationVisible(loanApplication))
                        list.add(loanApplication);
                }

                LoadingSemaphore loadingSemaphore = new LoadingSemaphore(list.size(), createListener());
                // Fixa o resultado, pois os próximos carregamentos serão apenas detalhamentos do que já foi carregado.
                loadingSemaphore.result = list;

                loadDataPaymentsAsync(context, list, loadingSemaphore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(context, databaseError);
            }
        });
    }

    private boolean checkLoanApplicationVisible(LoanApplication loanApplication) {
        // Nota: está comentado para que seja possível testar localmente com o mesmo usuário
        Date expirationDate = loanApplication.getExpirationDate();
        Date currentDate = DateUtils.getCurrentDate(false);
        return
            /*loanApplication.OwnerId == null || !loanApplication.OwnerId.equals(SystemInfo.Instance.LoggedUserID)
            &&*/
            expirationDate == null || expirationDate.compareTo(currentDate) >= 0;
    }

    private void loadDataPaymentsAsync(final Context context, final List<LoanApplication> loanApplications, final LoadingSemaphore loadingSemaphore) {
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

                if (list != null)
                    Collections.sort(list, new LoanApplicationExpirationAndCreationDateComparator(DateUtils.getCurrentDate(false)));

                mListener.OnLoadCompleted(list);
            }
        };
    }

    @Override
    public void OnClick(Context context, Object item) {
        Intent it = new Intent(context, LoanApplicationDetailActivity.class);
        it.putExtra(LoanApplicationDetailActivity.EXTRA_LOAN_APPLICATION_ITEM, (LoanApplication)item);
        context.startActivity(it);
    }
}
