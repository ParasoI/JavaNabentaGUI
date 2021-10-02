package nabenta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exceptions.IncorrectCredentialsException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Controller_ParentRoot {
    
    @FXML private TextField txfUsername;
    @FXML private TextField txfUserpass;
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    public void exit(ActionEvent e){
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public void login(ActionEvent event){
        
        //From View
        String username = txfUsername.getText();
        String userpass = txfUserpass.getText();
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String query = "SELECT employee_id, employee_type "
                + "FROM employee "
                + "WHERE employee_userName = BINARY '" + username + "' AND employee_userPass = BINARY '" + userpass + "';";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            
            //If there is an employee. . .
            if (result.isBeforeFirst() ) {    
                //positive
                result.next();
                int emp_id = result.getInt("employee_id");
                String emp_type = result.getString("employee_type");
                
                if(emp_type.equalsIgnoreCase("cashier")){
                    switchToCashier(event,emp_id);
                }else if(emp_type.equalsIgnoreCase("admin")){
                    switchToAdmin(event,emp_id);
                }
            } else{
                //custom exception
                throw new IncorrectCredentialsException();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }catch(IncorrectCredentialsException e){
            //Alert
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setHeaderText("Incorrect Credentials");
            message.setContentText("Your username or/and password is not registered. Please try again :)");
            message.showAndWait();
            //Clear View Textfields
            txfUsername.clear();
            txfUserpass.clear();
        }
    }
    
    public void switchToCashier(ActionEvent event,int cashier_id){
        try {
            setEmployee(cashier_id);
            Model_Tabs.setFxml("Cashier_TakeOrders.fxml");
            
            root = FXMLLoader.load(getClass().getResource("Cashier_TakeOrders.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Controller_ParentRoot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void switchToAdmin(ActionEvent event,int admin_id){
        try {
            setEmployee(admin_id);
            Model_Tabs.setFxml("Admin_Stocks.fxml");
            
            root = FXMLLoader.load(getClass().getResource("Admin_Stocks.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Controller_ParentRoot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setEmployee(int cashier_id){
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String query = "SELECT employee_firstName, employee_lastName, employee_picture "
                + "FROM employee "
                + "WHERE employee_id = " + cashier_id + ";";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            
            //If there is an employee. . .
            if (result.isBeforeFirst() ) {    
                //positive
                result.next();
                Model_Employee.setId(cashier_id);
                Model_Employee.setFirstName(result.getString("employee_firstName"));
                Model_Employee.setLastName(result.getString("employee_lastName"));
                Model_Employee.setPic(result.getBlob("employee_picture"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
}
