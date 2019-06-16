package e.investo.data;

import java.util.Date;

public class PaymentInfo {
    public String idPayment;
    public double TotalValue;
    public int ParcelsCount;
    public int ParcelsAlreadyPayed;
    public Date NextDueDate;
    public int NextParcelNumber;
    public double NextParcelValue;

    public String getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(String idPayment) {
        this.idPayment = idPayment;
    }
}
