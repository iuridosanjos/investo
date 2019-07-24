package e.investo.data;

import com.google.firebase.database.Exclude;

import java.util.List;

public class PaymentData {
    public String id;

    // Id do pedido de empréstimo
    public String loanApplicationId;
    // Id do investimento
    public String loanDataId;
    // Id do usuário pagador
    public String payerUserId;

    // Parcelas
    public List<PaymentParcel> parcels;

    public List<PaymentParcel> getParcels() {
        return parcels;
    }

    public void setParcels(List<PaymentParcel> parcels) {
        if (parcels != null)
            for (PaymentParcel paymentParcel : parcels)
                paymentParcel.paymentData = this;

        this.parcels = parcels;
    }
}
