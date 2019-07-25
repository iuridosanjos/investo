package e.investo.data.comparators;

import java.util.Comparator;

import e.investo.data.LoanApplication;
import e.investo.data.LoanData;

public class SingleLoanDataCreationDateComparator implements Comparator<LoanApplication> {
    @Override
    public int compare(LoanApplication o1, LoanApplication o2) {
        // Considera que terá apenas um "LoanData" dentro do "LoanApplication". Caso contrário, retorna qualquer ordem.
        int cmp = 0;

        if (o1.loanData != null && o2.loanData != null && o1.loanData.size() == 1 && o2.loanData.size() == 1) {
            LoanData loanData1 = o1.loanData.get(0);
            LoanData loanData2 = o2.loanData.get(0);

            cmp = Long.compare(loanData1.creationDateLong, loanData2.creationDateLong);
        }

        return cmp;
    }
}
