package e.investo.data;

import java.util.Date;

public class PaymentParcel {
    // Número da parcela. Deve ser considerado como PK (ID).
    public int number;
    // Data de vencimento
    public Date dueDate;
    // Valor da parcela
    public double value;

    // Data em que foi pago, se foi
    public Date payday;
}
