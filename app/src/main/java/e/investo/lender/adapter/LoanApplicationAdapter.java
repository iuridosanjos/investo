package e.investo.lender.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import e.investo.R;
import e.investo.data.LoanApplication;

public class LoanApplicationAdapter extends BaseAdapter {

    private static DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###.00");

    private Context mContext;
    private LoanApplication[] mLoans;

    public LoanApplicationAdapter(Context c, LoanApplication[] loans){
        mContext = c;
        mLoans = loans;
    }

    @Override
    public int getCount() {
        return (mLoans != null) ? mLoans.length : 0;
    }

    @Override
    public Object getItem(int i) {
        return mLoans[i];
    }

    @Override
    public long getItemId(int i) {
        return mLoans[i].Id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LoanApplication loan = mLoans[i];

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.loan_application_item_view, null);

            holder = new ViewHolder();
            holder.imgLogo = (ImageView) convertView.findViewById(R.id.imgEstablishmentLogo);
            holder.txtEstablishmentName = (TextView) convertView.findViewById(R.id.txtEstablishmentName);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.txtRequestedValue = (TextView) convertView.findViewById(R.id.txtRequestedValue);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        //holder.imgLogo.setImageResource(R.drawable.icon);
        holder.txtEstablishmentName.setText(loan.EstablishmentName);
        holder.txtAddress.setText(loan.Address);
        holder.txtRequestedValue.setText("R$ " + CURRENCY_FORMAT.format(loan.RequestedValue));

        return convertView;
    }

    static class ViewHolder {
        ImageView imgLogo;
        TextView txtEstablishmentName;
        TextView txtAddress;
        TextView txtRequestedValue;
    }
}
