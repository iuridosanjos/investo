package e.investo.data;

public enum LoanApplicationStatus {

    // Análise de crédito pendente
    PENDING_CREDIT_ANALYSIS("Análise de crédito pendente"),

    // Aprovação/confirmação pendente pelo usuário
    PENDING_APPROVAL("Aprovação pendente"),

    // Válido, ou seja, tem todas as aprovações necessárias
    VALID("Válido");

    public String Description;

    LoanApplicationStatus (String description) {
        Description = description;
    }

    @Override
    public String toString() {
        return Description;
    }
}
