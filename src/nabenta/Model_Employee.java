package nabenta;

import java.sql.Blob;

public class Model_Employee {
    
    private static int id;
    private static String firstName;
    private static String lastName;
    private static Blob pic;
    
    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Model_Employee.id = id;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        Model_Employee.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Model_Employee.lastName = lastName;
    }

    public static Blob getPic() {
        return pic;
    }

    public static void setPic(Blob pic) {
        Model_Employee.pic = pic;
    }
    
    
    
}