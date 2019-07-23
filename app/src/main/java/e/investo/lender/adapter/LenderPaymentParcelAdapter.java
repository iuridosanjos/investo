package e.investo.lender.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.data.PaymentParcel;

public class LenderPaymentParcelAdapter extends BaseAdapter {

    private Context mContext;
    private List<PaymentParcel> mParcels;

    public LenderPaymentParcelAdapter(Context c, List<PaymentParcel> parcels){
        mContext = c;
        mParcels = parcels;
    }

    @Override
    public int getCount() {
        return (mParcels != null) ? mParcels.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mParcels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        PaymentParcel parcel = mParcels.get(i);

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.parcel_item_view, null);

            holder = new ViewHolder();
            holder.txtParcelNumber = (TextView) convertView.findViewById(R.id.txtParcelNumber);
            holder.txtDueDate = (TextView) convertView.findViewById(R.id.txtDueDate);
            holder.txtValueInfo = (TextView) convertView.findViewById(R.id.txtValueInfo);
            holder.txtPaymentStatus = (TextView) convertView.findViewById(R.id.txtPaymentStatus);
            holder.llRoot = (LinearLayout) convertView.findViewById(R.id.linear_layout_root);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtParcelNumber.setText(String.format("%02d", parcel.number + 1));
        holder.txtDueDate.setText(String.format(mContext.getString(R.string.prompt_due_date) + " %s", CommonFormats.DATE_FORMAT.format(parcel.getDueDate())));
        holder.txtValueInfo.setText(CommonFormats.CURRENCY_FORMAT.format(parcel.value));
        holder.txtPaymentStatus.setText(getPaymentStatus(parcel));
        holder.llRoot.setAlpha(getAlpha(parcel));

        return convertView;
    }

    private String getPaymentStatus(PaymentParcel parcel)
    {
        if (parcel.getPayday() == null)
            return mContext.getString(R.string.lender_payment_status_pending);
        else
            return String.format(mContext.getString(R.string.lender_payment_status_payed), CommonFormats.DATE_FORMAT.format(parcel.getPayday()));
    }

    private float getAlpha(PaymentParcel parcel)
    {
        return parcel.getPayday() == null ? 1f : 0.4f;
    }

    static class ViewHolder {
        TextView txtParcelNumber;
        TextView txtDueDate;
        TextView txtValueInfo;
        TextView txtPaymentStatus;
        LinearLayout llRoot;
    }
}
