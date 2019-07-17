package e.investo.data;

import java.io.Serializable;
import java.util.Date;

public class LoanData implements Serializable {

    public String id;

    public String idUser;
    public String idApplication;

    public Date dataCriacao;
    public Double valorEmprestimo;

    // Dados de pagamento. Dados das parcelas.
    public PaymentData paymentData;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Double getValorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(Double valorEmprestimo) {
        this.valorEmprestimo = valorEmprestimo;
    }

    public String getIdApplication() {
        return idApplication;
    }

    public void setIdApplication(String idApplication) {
        this.idApplication = idApplication;
    }
}
