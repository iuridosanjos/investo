package e.investo.data;

import java.io.Serializable;

public class User implements Serializable {
    public String Id;

    public String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIdAplication() {
        return Id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
