package e.investo.data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    // Dia do vencimento
    public int DueDay;

    public String Address;

    // Descrição escrita pelo usuário para justificar o pedido de empréstimo. Opcional.
    public String Description;

    //Informações realizadas com os dados sincronizados.
    @Exclude
    public List<LoanData> loanData;

    // Data de criação. Em long porque o Firebase não armazena o campo do tipo Date do Java corretamente.
    public long CreationDateLong;

    // Data de expiração. Em long porque o Firebase não armazena o campo do tipo Date do Java corretamente.
    public long ExpirationDateLong;

    public String getIdAplication() {
        return idAplication;
    }

    public void setIdAplication(String idAplication) {
        this.idAplication = idAplication;
    }

    @Exclude
    public Date getCreationDate() {
        return CreationDateLong <= 0 ? null : new Date(CreationDateLong);
    }
    public void setCreationDate(Date creationDate) {
        CreationDateLong = creationDate == null ? 0 : creationDate.getTime();
    }

    @Exclude
    public Date getExpirationDate() {
        return ExpirationDateLong <= 0 ? null : new Date(ExpirationDateLong);
    }
    public void setExpirationDate(Date expirationDate) {
        ExpirationDateLong = expirationDate == null ? 0 : expirationDate.getTime();
    }

    @Exclude
    public double getRemainingValue() {
        double remainingValue = RequestedValue;
        if (loanData != null)
            for (LoanData loanData : this.loanData)
                remainingValue -= loanData.value;

        return remainingValue;
    }
}
