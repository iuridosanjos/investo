package e.investo.data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;

public class LoanData implements Serializable {

    public String id;

    public String idUser;
    public String idApplication;

    public long creationDateLong;
    // Valor emprestado. Nota: esse valor ainda deve ter as taxas adicionadas para as parcelas.
    public double value;

    // Dados de pagamento. Dados das parcelas.
    public PaymentData paymentData;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Exclude
    public Date getCreationDate() {
        return new Date(creationDateLong);
    }

    public void setCreationDate(Date creationDate) {
        creationDateLong = creationDate.getTime();
    }

    public String getIdApplication() {
        return idApplication;
    }

    public void setIdApplication(String idApplication) {
        this.idApplication = idApplication;
    }
}
