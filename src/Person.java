import java.util.ArrayList;

public class Person {

    int number ;
    String name ;
    String password ;
    String key ;
    ArrayList<Others> otherPersons ;


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<Others> getOtherPersons() {
        return otherPersons;
    }

    public void setOtherPersons(ArrayList<Others> otherPersons) {
        this.otherPersons = otherPersons;
    }
}
