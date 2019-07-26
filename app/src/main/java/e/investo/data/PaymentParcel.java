package e.investo.data;

import com.google.firebase.database.Exclude;

import java.util.Date;

import e.investo.common.DateUtils;

public class PaymentParcel implements Comparable<PaymentParcel> {
    // Número da parcela. Deve ser considerado como PK (ID).
    public int number;
    // Data de vencimento
    public long dueDateLong;
    // Valor da parcela
    public double value;

    // Data em que foi pago, se foi
    public long paydayLong;

    @Exclude
    public PaymentData paymentData;

    @Exclude
    public Date getDueDate()
    {
        return new Date(dueDateLong);
    }

    public void setDueDate(Date date)
    {
        dueDateLong = date.getTime();
    }

    @Exclude
    public Date getPayday()
    {
        if (paydayLong <= 0)
            return null;
        else
            return new Date(paydayLong);
    }

    public void setPayday(Date date)
    {
        if (date == null)
            paydayLong = 0;
        else
            paydayLong = date.getTime();
    }

    @Override
    public int compareTo(PaymentParcel o) {
        return Integer.compare(number, o.number);
    }

    @Exclude
    public boolean isLatePayment() {
        if (getPayday() != null)
            return false;

        Date dueDate = getDueDate();
        Date currentDate = DateUtils.getCurrentDate(false);

        return dueDate.compareTo(currentDate) < 0;
    }
}
