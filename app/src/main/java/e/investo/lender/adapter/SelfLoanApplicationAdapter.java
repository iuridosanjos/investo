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
import e.investo.data.LoanApplication;
import e.investo.data.PaymentInfo;

public class SelfLoanApplicationAdapter extends BaseAdapter {

    private Context mContext;
    private List<LoanApplication> mLoans;

    public SelfLoanApplicationAdapter(Context c, List<LoanApplication> loans){
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
        return mLoans.get(i).Id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LoanApplication loan = mLoans.get(i);

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.loan_application_item_view_self, null);

            holder = new ViewHolder();
            //holder.imgLogo = (ImageView) convertView.findViewById(R.id.imgEstablishmentLogo);
            holder.txtEstablishmentName = (TextView) convertView.findViewById(R.id.txtEstablishmentName);
            holder.txtEstablishmentType = (TextView) convertView.findViewById(R.id.txtEstablishmentType);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.txtValueInfo = (TextView) convertView.findViewById(R.id.txtValueInfo);
            holder.txtPaymentInfo1 = (TextView) convertView.findViewById(R.id.txtPaymentInfo1);
            holder.txtPaymentInfo2 = (TextView) convertView.findViewById(R.id.txtPaymentInfo2);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        //holder.imgLogo.setImageResource(R.drawable.icon);
        holder.txtEstablishmentName.setText(loan.EstablishmentName.toUpperCase());
        holder.txtEstablishmentType.setText(loan.EstablishmentType.toUpperCase());
        holder.txtAddress.setText(loan.Address.toUpperCase());
        holder.txtValueInfo.setText(getValueInfo(loan));
        holder.txtPaymentInfo1.setText(getPaymentStatus(loan.PaymentInfo).toUpperCase());
        holder.txtPaymentInfo2.setText((getPaymentParcelsInfo(loan.PaymentInfo)));

        return convertView;
    }

    private String getValueInfo(LoanApplication loan) {

        if (loan.PaymentInfo == null)
            return "Sem registros de pagamento";

        return String.format("%s em %sx (%s%% a.m.)", CommonFormats.CURRENCY_FORMAT.format(loan.PaymentInfo.TotalValue), loan.PaymentInfo.ParcelsCount, loan.MonthlyInterests);
    }

    private String getPaymentStatus(PaymentInfo paymentInfo)
    {
        Date currentTime = Calendar.getInstance().getTime();

        if (paymentInfo == null)
            return "Sem registros de pagamento";
        else if (paymentInfo.NextDueDate.compareTo(currentTime) < 0)
        { // Pagamento atrasado
            return String.format("Atraso de %s parcelas", Math.max(0, paymentInfo.NextParcelNumber - paymentInfo.ParcelsAlreadyPayed - 1), CommonFormats.DATE_FORMAT.format(paymentInfo.NextDueDate));
        }
        else if (paymentInfo.ParcelsAlreadyPayed > 0)
        { // Esperando próximo pagamento, mas algumas parcelas já foram pagas
            return String.format("Pagamento em situação normal", paymentInfo.ParcelsAlreadyPayed);
        }
        else
        { // Esperando primeiro pagamento
            return String.format("Aguardando pagamento", CommonFormats.DATE_FORMAT.format(paymentInfo.NextDueDate));
        }
    }
    private String getPaymentParcelsInfo(PaymentInfo paymentInfo)
    {
        if (paymentInfo == null)
            return "";

        return String.format("Parcela %s/%s de %s com venc. %s", paymentInfo.NextParcelNumber, paymentInfo.ParcelsCount, CommonFormats.CURRENCY_FORMAT.format(paymentInfo.NextParcelValue), CommonFormats.DATE_FORMAT.format(paymentInfo.NextDueDate));
    }

    static class ViewHolder {
        TextView txtEstablishmentName;
        TextView txtEstablishmentType;
        TextView txtAddress;
        TextView txtValueInfo;
        TextView txtPaymentInfo1;
        TextView txtPaymentInfo2;
    }
}
