package e.investo.borrower.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.common.ErrorHandler;
import e.investo.conection.Connection;
import e.investo.data.LoanData;
import e.investo.data.LoanApplication;

public class BorrowerLoanApplicationsAdapter extends BaseAdapter {

    private Context mContext;
    private List<LoanApplication> mLoans;

    public BorrowerLoanApplicationsAdapter(Context c, List<LoanApplication> loans){
        mContext = c;
        mLoans = loans;
    }

    @Override
    public int getCount() {
        return (mLoans != null) ? mLoans.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mLoans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final LoanApplication loan = mLoans.get(i);

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.loan_application_item_view_borrower, null);

            holder = new ViewHolder();
            holder.txtEstablishmentName = (TextView) convertView.findViewById(R.id.txtEstablishmentName);
            holder.txtEstablishmentType = (TextView) convertView.findViewById(R.id.txtEstablishmentType);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.txtValueInfo = (TextView) convertView.findViewById(R.id.txtValueInfo);
            holder.txtLoanReached = (TextView) convertView.findViewById(R.id.txtLoanReached);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtEstablishmentName.setText(loan.EstablishmentName.toUpperCase());
        holder.txtEstablishmentType.setText(loan.EstablishmentType.toUpperCase());
        holder.txtAddress.setText(loan.Address.toUpperCase());
        holder.txtValueInfo.setText(getValueInfo(loan));
        holder.txtLoanReached.setText(getLoanReached(loan).toUpperCase());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeLoanApplication(loan);
            }
        });

        return convertView;
    }

    private String getValueInfo(LoanApplication loan) {
        return String.format("%s em %sx (%s a.m.)", CommonFormats.CURRENCY_FORMAT.format(loan.RequestedValue), loan.ParcelsAmount, CommonFormats.PERCENTAGE_FORMAT.format(loan.MonthlyInterests * 100));
    }

    private String getLoanReached(LoanApplication loan) {
        double totalValue = 0;
        if (loan.loanData != null)
            for (LoanData loanData : loan.loanData)
                totalValue += loanData.valorEmprestimo;

        return String.format("Adquirido até então: %s", CommonFormats.CURRENCY_FORMAT.format(totalValue));
    }

    private void removeLoanApplication(LoanApplication loanApplication)
    {
        final DatabaseReference databaseReference = Connection.GetDatabaseReference();
        final String idApplication = loanApplication.getIdAplication();

        // Remove os investimentos primeiro
        Query query = databaseReference.child("Investimento").orderByChild("idApplication").equalTo(idApplication);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LoanData> list = new ArrayList<>();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    objSnapshot.getRef().removeValue();
                }

                // Remove a aplicação em si
                databaseReference.child("Aplicacoes").child(idApplication).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) // Erro
                            ErrorHandler.Handle(mContext, databaseError);
                        else
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.loan_application_removed), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorHandler.Handle(mContext, databaseError);
            }
        });
    }

    static class ViewHolder {
        TextView txtEstablishmentName;
        TextView txtEstablishmentType;
        TextView txtAddress;
        TextView txtValueInfo;
        TextView txtLoanReached;
        ImageButton btnDelete;
    }
}
