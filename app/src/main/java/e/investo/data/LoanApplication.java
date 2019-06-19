package e.investo.data;

import java.io.Serializable;
import java.util.Date;

// Representa um pedido de empréstimo
public class LoanApplication implements Serializable {
    public String idAplication;

    public String OwnerId;
    public String OwnerName;

    public String EstablishmentName;
    public String CNPJ;
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

    //Informações realizadas com os dados sincronizados.
    public DataPayment DataPayment;

    public Date CreationDate;

    public String getIdAplication() {
        return idAplication;
    }

    public void setIdAplication(String idAplication) {
        this.idAplication = idAplication;
    }

}
