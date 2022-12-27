import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class javaDB {


    public static void main(String[] args)
    {
        //getConnection() ;
    }
    public static Connection getConnection()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver"); //Connector with database
            String url = "jdbc:mysql://localhost:3306/" ; //java data base connectivity
            String  dataBaseName = "finalProject" ;
            String  userName = "root" ;
            String  password = "" ;
            Connection connection = DriverManager.getConnection(url+dataBaseName , userName ,password );
            System.out.println("Connected successfully");
            return connection ;
        }
        catch (Exception ex)
        {
            System.out.println("Could not connect with data base");
            ex.printStackTrace();
        }

        return null;
    }

    public static void insert() //put the parameter of the function
    {
        Connection connection = getConnection();
        try {
            String sqlInsertStatement = "INSERT INTO 'column name'('id' , 'name' , 'age') Values( , , )" ; //the statement for inserting the data on the DB
            PreparedStatement statement = connection.prepareStatement(sqlInsertStatement);

        }catch(SQLException ex)
        {
            System.out.println("Could not insert data");
            ex.printStackTrace();
        }}

}
