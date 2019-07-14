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
import java.util.List;

import e.investo.R;
import e.investo.common.CommonFormats;
import e.investo.common.ErrorHandler;
import e.investo.connection.Connection;
import e.investo.data.LoanApplication;
import e.investo.data.LoanData;
import e.investo.data.PaymentParcel;

public class PaymentParcelAdapter extends BaseAdapter {

    private Context mContext;
    private List<PaymentParcel> mParcels;

    public PaymentParcelAdapter(Context c, List<PaymentParcel> parcels){
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

        holder.txtParcelNumber.setText(String.format("%02d", parcel.number));
        holder.txtDueDate.setText(String.format("Vencimento: %s", CommonFormats.DATE_FORMAT.format(parcel.dueDate)));
        holder.txtValueInfo.setText(CommonFormats.CURRENCY_FORMAT.format(parcel.value));
        holder.txtPaymentStatus.setText(getPaymentStatus(parcel));
        holder.llRoot.setAlpha(getAlpha(parcel));

        return convertView;
    }

    private String getPaymentStatus(PaymentParcel parcel)
    {
        if (parcel.payday == null)
            return "Pagamento pendente";
        else
            return String.format("Pago em %s", CommonFormats.DATE_FORMAT.format(parcel.payday));
    }

    private float getAlpha(PaymentParcel parcel)
    {
        return parcel.payday == null ? 1f : 0.4f;
    }

    static class ViewHolder {
        TextView txtParcelNumber;
        TextView txtDueDate;
        TextView txtValueInfo;
        TextView txtPaymentStatus;
        LinearLayout llRoot;
    }
}
