package e.investo.data.comparators;

import java.util.Comparator;
import java.util.Date;

import e.investo.data.LoanApplication;

public class LoanApplicationExpirationAndCreationDateComparator implements Comparator<LoanApplication> {
    private Date currentDate;

    public LoanApplicationExpirationAndCreationDateComparator(Date currentDateWithoutTime) {
        currentDate = currentDateWithoutTime;
    }

    @Override
    public int compare(LoanApplication o1, LoanApplication o2) {
        boolean isExpired1 = o1.isExpired(currentDate);
        boolean isExpired2 = o2.isExpired(currentDate);

        int cmp = 0;

        if (isExpired1 && !isExpired2)
            cmp = 1;
        else if (!isExpired1 && isExpired2)
            cmp = -1;
        else if (isExpired1 && isExpired2)
            cmp = 0;
        else {
            Date creationDate1 = o1.getCreationDate();
            Date creationDate2 = o2.getCreationDate();

            cmp = creationDate1.compareTo(creationDate2);
        }

        return cmp;
    }
}
