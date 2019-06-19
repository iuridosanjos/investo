package e.investo.data;

import java.util.Date;

public class DataPayment {

    public String idUser;


    public String idAplication;


    public Date dataCriacao;
    public Double valorEmprestimo;

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

    public String getIdAplication() {
        return idAplication;
    }

    public void setIdAplication(String idAplication) {
        this.idAplication = idAplication;
    }
}
