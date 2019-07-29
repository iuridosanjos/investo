package e.investo.borrower.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import e.investo.R;
import e.investo.business.PaymentController;
import e.investo.common.CommonConstants;
import e.investo.common.CommonFormats;
import e.investo.common.DateUtils;
import e.investo.common.ErrorHandler;
import e.investo.connection.Connection;
import e.investo.data.InterestsAndFineResult;
import e.investo.data.LoanApplication;
import e.investo.data.LoanData;
import e.investo.data.PaymentParcel;
import e.investo.data.PaymentParcelUnion;

public class BorrowerPaymentParcelAdapter extends BaseAdapter {

    private Context mContext;
    private List<PaymentParcelUnion> mParcels;

    public BorrowerPaymentParcelAdapter(Context c, List<PaymentParcelUnion> parcels){
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
        PaymentParcelUnion parcelUnion = mParcels.get(i);
        PaymentParcel parcel = parcelUnion.uniqueParcel;

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.parcel_item_view, null);

            holder = new ViewHolder();
            holder.txtParcelNumber = (TextView) convertView.findViewById(R.id.txtParcelNumber);
            holder.txtDueDate = (TextView) convertView.findViewById(R.id.txtDueDate);
            holder.txtValueInfo = (TextView) convertView.findViewById(R.id.txtValueInfo);
            holder.txtPaymentStatus = (TextView) convertView.findViewById(R.id.txtPaymentStatus);
            holder.llRoot = (LinearLayout) convertView.findViewById(R.id.linear_layout_root);
            holder.txtFineInfo = convertView.findViewById(R.id.txtFineInfo);
            holder.txtInterestsInfo = convertView.findViewById(R.id.txtInterestsInfo);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        int missingDueDateDays = getMissingDueDateDays(parcel);

        holder.txtParcelNumber.setText(String.format("%02d", parcel.number + 1));
        holder.txtDueDate.setText(getdueDateInfo(parcel, missingDueDateDays));

        holder.txtPaymentStatus.setText(getPaymentStatus(parcel, missingDueDateDays));
        holder.llRoot.setAlpha(getAlpha(parcel));

        boolean isLatePayment = parcel.getPayday() == null && missingDueDateDays < 0;
        if (isLatePayment) // Atrasado. Mostra informações de juros e multa.
        {
            InterestsAndFineResult interestsAndFineResult = PaymentController.calculateInterestsAndFine(parcel.value, missingDueDateDays * (-1));

            holder.txtFineInfo.setVisibility(View.VISIBLE);
            holder.txtInterestsInfo.setVisibility(View.VISIBLE);

            holder.txtFineInfo.setText(getFineInfo(interestsAndFineResult));
            holder.txtInterestsInfo.setText(getInterestsInfo(interestsAndFineResult));

            holder.txtValueInfo.setText(getValueInfoOnLatePayment(interestsAndFineResult));
        }
        else {
            holder.txtValueInfo.setText(CommonFormats.CURRENCY_FORMAT.format(parcel.value));

            holder.txtFineInfo.setVisibility(View.GONE);
            holder.txtInterestsInfo.setVisibility(View.GONE);
        }

        updateColor(parcel, missingDueDateDays, holder);

        return convertView;
    }

    private String getFineInfo(InterestsAndFineResult interestsAndFineResult) {
        return String.format(mContext.getString(R.string.late_payment_fine_info),
                CommonFormats.CURRENCY_FORMAT.format(interestsAndFineResult.fineValue),
                CommonFormats.PERCENTAGE_FORMAT.format(interestsAndFineResult.fineFactor * 100));
    }

    private String getInterestsInfo(InterestsAndFineResult interestsAndFineResult) {
        return String.format(mContext.getString(R.string.late_payment_interests_info),
                CommonFormats.CURRENCY_FORMAT.format(interestsAndFineResult.interestsValue),
                CommonFormats.PERCENTAGE_FORMAT.format(interestsAndFineResult.interestsFactor * 100));
    }

    private String getValueInfoOnLatePayment(InterestsAndFineResult interestsAndFineResult) {
        return String.format("%s (original: %s)",
                CommonFormats.CURRENCY_FORMAT.format(interestsAndFineResult.adjustedValue),
                CommonFormats.CURRENCY_FORMAT.format(interestsAndFineResult.originalValue));
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
            return String.format(mContext.getString(R.string.borrower_payment_status_payed), CommonFormats.DATE_FORMAT.format(parcel.getPayday()));
        else if (missingDueDateDays >= 0)
            return mContext.getString(R.string.borrower_payment_status_pending);
        else
            return mContext.getString(R.string.borrower_payment_status_late);
    }

    private void updateColor(PaymentParcel parcel, int missingDueDateDays, ViewHolder viewHolder) {
        int color;

        if (parcel.getPayday() != null)
            color = mContext.getColor(R.color.parcelPaymentStatus_Payed);
        else if (missingDueDateDays < 0)
            color = mContext.getColor(R.color.parcelPaymentStatus_Late);
        else if (missingDueDateDays <= CommonConstants.DUE_DATE_MISSING_DAYS_ALERT)
            color = mContext.getColor(R.color.parcelPaymentStatus_Alert);
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
        TextView txtFineInfo;
        TextView txtInterestsInfo;
        LinearLayout llRoot;
    }
}
