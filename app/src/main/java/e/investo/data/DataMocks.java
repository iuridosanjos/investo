package e.investo.data;

import java.util.ArrayList;
import java.util.List;

public class DataMocks {

    public static final List<LoanApplication> LOAN_APPLICATIONS = _CreateDummyLoanApplications();

    public static final List<LoanApplication> LOGGED_USER_LOAN_APPLICATIONS = new ArrayList<>();

    private static List<LoanApplication> _CreateDummyLoanApplications()
    {
        List<LoanApplication> list = new ArrayList<>();

        LoanApplication app = new LoanApplication();
        app.Id = 1;
        app.EstablishmentName = "Java Café";
        app.EstablishmentType = "Comedoria";
        app.Owner = new User();
        app.Owner.id = "1";
        app.Owner.name = "Manoel Alquimo";
        app.Address = "Av. Agamenon Magalhães";
        app.RequestedValue = 5000;
        app.ParcelsAmount = 12;
        app.MonthlyInterests = 0.78;
        app.Description = "Quero melhorar o atendimento com uma nova máquina de café expresso";
        list.add(app);

        app = new LoanApplication();
        app.Id = 2;
        app.EstablishmentName = "Itália em casa";
        app.EstablishmentType = "Comedoria Delivery";
        app.Owner = new User();
        app.Owner.id = "2";
        app.Owner.name = "Josefina Marta";
        app.Address = "Av. Conde da Boa Vista";
        app.RequestedValue = 2700;
        app.ParcelsAmount = 8;
        app.MonthlyInterests = 0.46;
        app.Description = "Pretendo aumentar ainda mais o serviço, por isso preciso comprar um novo forno";
        list.add(app);

        return list;
    }

    public static void AddUserLoanApplication(LoanApplication loanApplication)
    {
        if (!contains(loanApplication.Id, LOGGED_USER_LOAN_APPLICATIONS))
            LOGGED_USER_LOAN_APPLICATIONS.add(loanApplication);
    }
    private static boolean contains(long id, List<LoanApplication> applications)
    {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).Id == id)
                return true;
        }

        return false;
    }
}
