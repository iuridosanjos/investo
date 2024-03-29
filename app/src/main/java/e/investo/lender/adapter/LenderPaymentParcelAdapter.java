package e.investo.lender.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import e.investo.R;
import e.investo.borrower.adapter.BorrowerPaymentParcelAdapter;
import e.investo.common.CommonConstants;
import e.investo.common.CommonFormats;
import e.investo.common.DateUtils;
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

        int missingDueDateDays = getMissingDueDateDays(parcel);

        holder.txtParcelNumber.setText(String.format("%02d", parcel.number + 1));
        holder.txtDueDate.setText(getdueDateInfo(parcel, missingDueDateDays));
        holder.txtValueInfo.setText(CommonFormats.CURRENCY_FORMAT.format(parcel.value));
        holder.txtPaymentStatus.setText(getPaymentStatus(parcel, missingDueDateDays));
        holder.llRoot.setAlpha(getAlpha(parcel));

        updateColor(parcel, missingDueDateDays, holder);

        return convertView;
    }

    private int getMissingDueDateDays(PaymentParcel parcel) {
        Date dueDate = parcel.getDueDate();
        Date currentDate = DateUtils.getCurrentDate(false);

        return DateUtils.daysBetweenDates(dueDate, currentDate);
    }

    private String getdueDateInfo(PaymentParcel parcel, int missingDueDateDays) {
        Date dueDate = parcel.getDueDate();
        String str = String.format(mContext.getString(R.string.due_date_with_value), CommonFormats.DATE_FORMAT.format(dueDate));

        String additionalString = null;
        if (parcel.getPayday() == null) {
            if (missingDueDateDays > 1 && missingDueDateDays <= 30)
                additionalString = String.format(mContext.getString(R.string.missing_x_days), String.valueOf(missingDueDateDays));
            else if (missingDueDateDays == 1)
                additionalString = mContext.getString(R.string.missing_1_day);
            else if (missingDueDateDays == 0)
                additionalString = mContext.getString(R.string.today);
            else if (missingDueDateDays == -1)
                additionalString = mContext.getString(R.string.late_payment_1_day);
            else if (missingDueDateDays < -1)
                additionalString = String.format(mContext.getString(R.string.late_payment_x_days), String.valueOf(missingDueDateDays * (-1)));
        }

        if (additionalString != null)
            str = str + " (" + additionalString + ")";

        return str;
    }

    private String getPaymentStatus(PaymentParcel parcel, int missingDueDateDays)
    {
        if (parcel.getPayday() != null)
            return String.format(mContext.getString(R.string.lender_payment_status_payed), CommonFormats.DATE_FORMAT.format(parcel.getPayday()));
        else if (missingDueDateDays >= 0)
            return mContext.getString(R.string.lender_payment_status_pending);
        else
            return mContext.getString(R.string.lender_payment_status_late);
    }

    private void updateColor(PaymentParcel parcel, int missingDueDateDays, ViewHolder viewHolder) {
        int color;

        if (parcel.getPayday() != null)
            color = mContext.getColor(R.color.parcelPaymentStatus_Payed);
        else if (missingDueDateDays < 0)
            color = mContext.getColor(R.color.parcelPaymentStatus_Late);
        else
            color = mContext.getColor(R.color.parcelPaymentStatus_Default);

        setColor(viewHolder, color);
    }

    private void setColor(ViewHolder viewHolder, int color) {
        viewHolder.txtParcelNumber.setBackgroundColor(color);
        viewHolder.txtPaymentStatus.setBackgroundColor(color);
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
