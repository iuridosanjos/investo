package e.investo.lender.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.common.DateUtils;
import e.investo.data.LoanApplication;

public class ListAllLoanApplicationAdapter extends BaseAdapter {

    private Context mContext;
    private List<LoanApplication> mLoans;

    public ListAllLoanApplicationAdapter(Context c, List<LoanApplication> loans){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_all_loan_application_item_view, null);

            holder = new ViewHolder();
            holder.txtEstablishmentName = (TextView) convertView.findViewById(R.id.txtEstablishmentName);
            holder.txtEstablishmentType = (TextView) convertView.findViewById(R.id.txtEstablishmentType);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.txtValueInfo = (TextView) convertView.findViewById(R.id.txtValueInfo);
            holder.txtExpirationDate = convertView.findViewById(R.id.txtExpirationDate);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtEstablishmentName.setText(loan.EstablishmentName.toUpperCase());
        holder.txtEstablishmentType.setText(loan.EstablishmentType.toUpperCase());
        holder.txtAddress.setText(loan.Address.toUpperCase());
        holder.txtValueInfo.setText(getValueInfo(loan));
        holder.txtExpirationDate.setText(getExpirationDateDescription(loan.getExpirationDate()));

        return convertView;
    }

    private String getValueInfo(LoanApplication loan)
    {
        double remainingValue = loan.getRemainingValue();
        return String.format("%s em %sx (%s a.m.)", CommonFormats.CURRENCY_FORMAT.format(remainingValue), loan.ParcelsAmount, CommonFormats.PERCENTAGE_FORMAT.format(loan.MonthlyInterests * 100));
    }

    private String getExpirationDateDescription(Date expirationDate)
    {
        if (expirationDate == null)
            return "Sem data de expiração"; // Campo será obrigatório?
        else {
            int remainingDays = DateUtils.daysBetweenDates(expirationDate, DateUtils.getCurrentDate(false));
            if (remainingDays < 0) // Já expirou, não deveria chegar aqui. Lançar exceção?
                return "Expirado";
            else
                return String.format("Expira em %s dias", remainingDays);
        }
    }

    static class ViewHolder {
        TextView txtEstablishmentName;
        TextView txtEstablishmentType;
        TextView txtAddress;
        TextView txtValueInfo;
        TextView txtExpirationDate;
    }
}
