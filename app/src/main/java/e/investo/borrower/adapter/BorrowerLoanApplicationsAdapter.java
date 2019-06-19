package e.investo.borrower.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import e.investo.R;
import e.investo.common.CommonFormats;
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
        LoanApplication loan = mLoans.get(i);

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.loan_application_item_view_borrower, null);

            holder = new ViewHolder();
            //holder.imgLogo = (ImageView) convertView.findViewById(R.id.imgEstablishmentLogo);
            holder.txtEstablishmentName = (TextView) convertView.findViewById(R.id.txtEstablishmentName);
            holder.txtEstablishmentType = (TextView) convertView.findViewById(R.id.txtEstablishmentType);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.txtValueInfo = (TextView) convertView.findViewById(R.id.txtValueInfo);
            holder.txtLoanReached = (TextView) convertView.findViewById(R.id.txtLoanReached);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        //holder.imgLogo.setImageResource(R.drawable.icon);
        holder.txtEstablishmentName.setText(loan.EstablishmentName.toUpperCase());
        holder.txtEstablishmentType.setText(loan.EstablishmentType.toUpperCase());
        holder.txtAddress.setText(loan.Address.toUpperCase());
        holder.txtValueInfo.setText(getValueInfo(loan));
        holder.txtLoanReached.setText(getLoanReached(loan).toUpperCase());

        return convertView;
    }

    private String getValueInfo(LoanApplication loan) {
        return String.format("%s em %sx (%s%% a.m.)", CommonFormats.CURRENCY_FORMAT.format(loan.RequestedValue), loan.ParcelsAmount, CommonFormats.PERCENTAGE_FORMAT.format(loan.MonthlyInterests * 100));
    }

    private String getLoanReached(LoanApplication loan) {
        // TODO: ajustar quando tiver as informações dos empréstimos realizados
        return String.format("Adquirido até então: %s", CommonFormats.CURRENCY_FORMAT.format(0));
    }

    static class ViewHolder {
        TextView txtEstablishmentName;
        TextView txtEstablishmentType;
        TextView txtAddress;
        TextView txtValueInfo;
        TextView txtLoanReached;
    }
}
