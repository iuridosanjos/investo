package e.investo.data;

import java.util.List;

public class PaymentParcelUnion {

    // Parcela única
    public PaymentParcel uniqueParcel;

    // Parcelas originais que foram unidas na única parcela
    public List<PaymentParcel> originalParcels;
}
