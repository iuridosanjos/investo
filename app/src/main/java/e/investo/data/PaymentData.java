package e.investo.data;

import java.util.List;

public class PaymentData {
    // Id do pedido de empréstimo
    public String loanAplicationId;
    // Id do investimento
    public String loanDataId;
    // Id do usuário pagador
    public String payerUserId;

    // Parcelas
    public List<PaymentParcel> parcels;
}
