package e.investo.data;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class PaymentParcel {
    // Número da parcela. Deve ser considerado como PK (ID).
    public int number;
    // Data de vencimento
    public long dueDateLong;
    // Valor da parcela
    public double value;

    // Data em que foi pago, se foi
    public long paydayLong;

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
}
