package e.investo.lender.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.data.LoanApplication;
import e.investo.data.PaymentInfo;

public class SelfLoanApplicationAdapter extends BaseAdapter {

    private Context mContext;
    private LoanApplication[] mLoans;

    public SelfLoanApplicationAdapter(Context c, LoanApplication[] loans){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.self_loan_application_item_view, null);

            holder = new ViewHolder();
            //holder.imgLogo = (ImageView) convertView.findViewById(R.id.imgEstablishmentLogo);
            holder.txtEstablishmentName = (TextView) convertView.findViewById(R.id.txtEstablishmentName);
            holder.txtEstablishmentType = (TextView) convertView.findViewById(R.id.txtEstablishmentType);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.txtValueInfo = (TextView) convertView.findViewById(R.id.txtValueInfo);
            holder.txtPaymentInfo = (TextView) convertView.findViewById(R.id.txtPaymentInfo);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        //holder.imgLogo.setImageResource(R.drawable.icon);
        holder.txtEstablishmentName.setText(loan.EstablishmentName);
        holder.txtEstablishmentType.setText(loan.EstablishmentType);
        holder.txtAddress.setText(loan.Address);
        holder.txtValueInfo.setText(String.format("%s em %sx (%s%% a.m.)", CommonFormats.CURRENCY_FORMAT.format(loan.RequestedValue), loan.ParcelsAmount, loan.MonthlyInterests));
        holder.txtPaymentInfo.setText(getPaymentInfo(loan.PaymentInfo));

        return convertView;
    }

    private String getPaymentInfo(PaymentInfo paymentInfo)
    {
        Date currentTime = Calendar.getInstance().getTime();

        if (paymentInfo == null)
            return "Sem registros de pagamento.";
        else if (paymentInfo.NextDueDate.compareTo(currentTime) > 0)
        { // Pagamento atrasado
            return String.format("Atraso de %s parcelas. %s de %s parcelas pagas.", Math.max(0, paymentInfo.NextParcelNumber - paymentInfo.ParcelsAlreadyPayed - 1), paymentInfo.ParcelsAlreadyPayed, paymentInfo.ParcelsCount);
        }
        else if (paymentInfo.ParcelsAlreadyPayed > 0)
        { // Esperando próximo pagamento, mas algumas parcelas já foram pagas
            return String.format("Em situação normal. %s de %s parcelas pagas. Próximo vencimento em %s.", paymentInfo.ParcelsAlreadyPayed, paymentInfo.ParcelsCount, CommonFormats.DATE_FORMAT.format(paymentInfo.NextDueDate));
        }
        else
        { // Esperando primeiro pagamento
            return String.format("Em situação normal. Próximo vencimento em %s.", CommonFormats.DATE_FORMAT.format(paymentInfo.NextDueDate));
        }
    }

    static class ViewHolder {
        TextView txtEstablishmentName;
        TextView txtEstablishmentType;
        TextView txtAddress;
        TextView txtValueInfo;
        TextView txtPaymentInfo;
    }
}
