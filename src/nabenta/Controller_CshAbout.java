package nabenta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author SAFE
 */
public class Controller_CshAbout implements Initializable{
    
    @FXML private ImageView imv_cashierPic;
    @FXML private Label lbl_cashierName;
    Stage stage;
    Scene scene;
    Parent root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Cashier init
        lbl_cashierName.setText(Model_Employee.getFirstName() + " " + Model_Employee.getLastName());
        try {
            InputStream binaryStream = Model_Employee.getPic().getBinaryStream(1, Model_Employee.getPic().length());
            Image cashierImage = new Image(binaryStream);
            
            imv_cashierPic.setImage(cashierImage);
        } catch (SQLException ex) {
            Logger.getLogger(Controller_CshTakeOrders.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void exit(ActionEvent e){
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public void switchToTakeOrder(ActionEvent e){
        if(!Model_Tabs.getFxml().equals("Cashier_TakeOrders.fxml")){
            try {
                Model_Tabs.setFxml("Cashier_TakeOrders.fxml");
                root = FXMLLoader.load(getClass().getResource("Cashier_TakeOrders.fxml"));
                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public void switchToSales(ActionEvent e){
        if(!Model_Tabs.getFxml().equals("Cashier_Sales.fxml")){
            try {
                Model_Tabs.setFxml("Cashier_Sales.fxml");
                root = FXMLLoader.load(getClass().getResource("Cashier_Sales.fxml"));
                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public void switchToAbout(ActionEvent e){
         if(!Model_Tabs.getFxml().equals("Cashier_About.fxml")){
            try {
                Model_Tabs.setFxml("Cashier_About.fxml");
                root = FXMLLoader.load(getClass().getResource("Cashier_About.fxml"));
                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public void switchToParentRoot(ActionEvent e){
        Alert msg = new Alert(Alert.AlertType.CONFIRMATION);
        msg.setTitle("Warning");
        msg.setHeaderText("SIGNING OUT");
        msg.setContentText("You are about to SIGN OUT. Do you want to continue?");
        
        Optional<ButtonType> response = msg.showAndWait();
        if(response.get() == ButtonType.OK){
            try {
                root = FXMLLoader.load(getClass().getResource("ParentRoot.fxml"));

                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(Controller_ParentRoot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
