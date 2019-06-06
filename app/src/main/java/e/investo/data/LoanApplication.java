package e.investo.data;

import java.io.Serializable;

public class LoanApplication implements Serializable {
    public long Id;
    public User Owner;
    public String EstablishmentName;
    public double RequestedValue;
    public String Address;
}
