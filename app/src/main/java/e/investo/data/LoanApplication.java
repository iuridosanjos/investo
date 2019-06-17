package e.investo.data;

import java.io.Serializable;

// Representa um pedido de empréstimo
public class LoanApplication implements Serializable {
    public long Id;
    public String idAplication;


    public User Owner;

    public String EstablishmentName;
    public String EstablishmentType;

    // Valor requisitado no empréstimo.
    public double RequestedValue;
    // Quantidade de parcelas para o pagamento. Expressa em meses.
    public int ParcelsAmount;
    // Juros ao mês
    public double MonthlyInterests;

    public String Address;

    // Descrição escrita pelo usuário para justificar o pedido de empréstimo. Opcional.
    public String Description;

    // Informações de pagamento do pedido de empréstimo.
    public PaymentInfo PaymentInfo;

    // Status do pedido de empréstimo
    public LoanApplicationStatus Status;

    public String getIdAplication() {
        return idAplication;
    }

    public void setIdAplication(String idAplication) {
        this.idAplication = idAplication;
    }

}
