package nabenta;

import exceptions.BlankFieldException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author SAFE
 */
public class Controller_AdmStocks implements Initializable{
    
    @FXML private ImageView imv_cashierPic;
    @FXML private Label lbl_cashierName;
    @FXML private TableView tbvGroceries;
    @FXML private TableColumn tbcBarcode;
    @FXML private TableColumn tbcName;
    @FXML private TableColumn tbcStocks;
    @FXML private TextField txfGroBarcode;
    @FXML private TextField txfGroName;
    @FXML private TextField txfGroPrice;
    @FXML private TextField txfGroCateg;
    @FXML private TextField txfGroDesc;
    @FXML private TextField txfSupBarcode;
    @FXML private TextField txfSupName;
    @FXML private TextField txfSupQuant;
    private ObservableList groceryData = FXCollections.observableArrayList();
    Stage stage;
    Scene scene;
    Parent root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryGroceries = "SELECT * "
                + "FROM grocery ;";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(queryGroceries);
            
            //GROCERIES
            while (result.next()) {                
                groceryData.add(new Model_Grocery(
                        result.getLong("grocery_barcode"),
                        result.getString("grocery_itemName"),
                        result.getInt("grocery_price"),
                        result.getInt("grocery_stocksLeft"),
                        result.getString("grocery_category"),
                        result.getString("grocery_description")
                    ));
            }
            
            tbcBarcode.setCellValueFactory(new PropertyValueFactory("barcode"));
            tbcName.setCellValueFactory(new PropertyValueFactory("name"));
            tbcStocks.setCellValueFactory(new PropertyValueFactory("stocks"));
            tbvGroceries.setItems(groceryData);
            
            tbvGroceries.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    try{
                        Model_Grocery selRow = (Model_Grocery) tbvGroceries.getSelectionModel().getSelectedItem();
                        if(selRow != null){
                            txfGroBarcode.setText(String.valueOf(selRow.getBarcode()));
                            txfGroName.setText(selRow.getName());
                            txfGroPrice.setText(Integer.toString(selRow.getPrice()));
                            txfGroCateg.setText(selRow.getCategory());
                            txfGroDesc.setText(selRow.getDescription());
                            
                            txfSupBarcode.setText(String.valueOf(selRow.getBarcode()));
                        }else{
                            throw new Exception();
                        }
                    }catch(Exception e){
                        
                    }
                }
            });
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
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
    
    public void switchToStocks(ActionEvent e){
        if(!Model_Tabs.getFxml().equals("Admin_Stocks.fxml")){
            try {
                Model_Tabs.setFxml("Admin_Stocks.fxml");
                root = FXMLLoader.load(getClass().getResource("Admin_Stocks.fxml"));
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
        if(!Model_Tabs.getFxml().equals("Admin_Sales.fxml")){
            try {
                Model_Tabs.setFxml("Admin_Sales.fxml");
                root = FXMLLoader.load(getClass().getResource("Admin_Sales.fxml"));
                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public void switchToReceipts(ActionEvent e){
         if(!Model_Tabs.getFxml().equals("Admin_Receipts.fxml")){
            try {
                Model_Tabs.setFxml("Admin_Receipts.fxml");
                root = FXMLLoader.load(getClass().getResource("Admin_Receipts.fxml"));
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
    
    public void deleteGrocery(ActionEvent e){
        try{
            Long fieldBarcode = Long.parseLong(txfGroBarcode.getText());
            Model_Grocery selGrocery = (Model_Grocery)tbvGroceries.getSelectionModel().getSelectedItem();
            
            if(fieldBarcode == selGrocery.getBarcode()){
                tbvGroceries.getItems().remove(tbvGroceries.getSelectionModel().getSelectedIndex());
                tbvGroceries.getSelectionModel().select(null);
                tbvGroceries.refresh();
                
                //From Database
                String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
                String dbUsername = "root";
                String dbPassword = "souleater24";
                String deleteSupply = "DELETE FROM supply "
                        + "WHERE supply_barcode = " + fieldBarcode +";";
                String deleteGrocery = "DELETE FROM grocery "
                        + "WHERE grocery_barcode = " + fieldBarcode +";";

                try{
                    Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                    Statement statement = connection.createStatement();
                    System.out.println(statement.executeUpdate(deleteSupply));
                    System.out.println(statement.executeUpdate(deleteGrocery));
                    
                }catch(SQLException ex){
                    ex.printStackTrace();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
                clearGroTextFields();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void saveGrocery(ActionEvent e){
        try{
            Long fieldBarcode = Long.parseLong(txfGroBarcode.getText());
            Model_Grocery selGrocery = (Model_Grocery)tbvGroceries.getSelectionModel().getSelectedItem();
            
            //From Database
            String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
            String dbUsername = "root";
            String dbPassword = "souleater24";
            String saveGrocery = "SELECT * "
                    + "FROM grocery "
                    + "WHERE grocery_barcode = " + fieldBarcode +";";

            try{
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(saveGrocery);
                
                if(result.next()){
                    //UPDATE DB
                    String fieldName = txfGroName.getText();
                    int fieldPrice = Integer.parseInt(txfGroPrice.getText());
                    String fieldCateg = txfGroCateg.getText();
                    String fieldDesc = txfGroDesc.getText();
                    
                    String groceryUpdate = "UPDATE grocery "
                            + "SET grocery_itemName = '" + fieldName + "', "
                            + "grocery_price = " + fieldPrice + ", "
                            + "grocery_category = '" + fieldCateg + "', "
                            + "grocery_description = '" + fieldDesc + "' "
                            + "WHERE grocery_barcode = " + fieldBarcode + ";";
                    
                    System.out.println(statement.executeUpdate(groceryUpdate));
                    
                    //UPDATE UI
                    selGrocery.setName(fieldName);
                    selGrocery.setPrice(fieldPrice);
                    selGrocery.setCategory(fieldCateg);
                    selGrocery.setDescription(fieldDesc);
                    tbvGroceries.refresh();
                }else{
                    //NEW ENTRY
                    //INSERT DB
                    String fieldName = txfGroName.getText();
                    int fieldPrice = Integer.parseInt(txfGroPrice.getText());
                    String fieldCateg = txfGroCateg.getText();
                    String fieldDesc = txfGroDesc.getText();
                    
                    String insertGrocery = "INSERT INTO grocery VALUES("
                            + fieldBarcode + ", "
                            + "'" + fieldName + "', "
                            + fieldPrice + ", "
                            + "'" + fieldCateg + "', "
                            + "'" + fieldDesc + "', "
                            + "DEFAULT); ";
                    
                    System.out.println(statement.executeUpdate(insertGrocery));
                    
                    //INSERT UI
                    Model_Grocery newGrocery = new Model_Grocery(fieldBarcode, 
                            fieldName, 
                            fieldPrice, 
                            0, 
                            fieldCateg, 
                            fieldDesc);
                    
                    tbvGroceries.getItems().add(newGrocery);
                    tbvGroceries.getSelectionModel().select(tbvGroceries.getItems().size() - 1);
                    tbvGroceries.refresh();
                    
                    
                }

            }catch(SQLException ex){
                ex.printStackTrace();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            
        }catch(Exception ex){
            
        }
    }
    
    public void addSupply(ActionEvent e){
        
        try{
            Model_Grocery selGrocery = (Model_Grocery)tbvGroceries.getSelectionModel().getSelectedItem();
            Long supBarcode = selGrocery.getBarcode();
            String supDate = getDateToday();
            String fieldSupName = txfSupName.getText();
            if(fieldSupName.equals("")){
                throw new BlankFieldException();
            }
            int fieldSupQuant = Integer.parseInt(txfSupQuant.getText());
            int supAdmin = Model_Employee.getId();

            //From Database
            String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
            String dbUsername = "root";
            String dbPassword = "souleater24";
            String insertSupplier = "INSERT INTO supply VALUES("
                    + supBarcode + ", "
                    + "'" + supDate + "', "
                    + "'" + fieldSupName + "', "
                    + fieldSupQuant + ", "
                    + supAdmin + ");";

            String updateGroStocks = "UPDATE grocery "
                    + "SET grocery_stocksLeft = grocery_stocksLeft + " + fieldSupQuant + " "
                    + "WHERE grocery_barcode = " + supBarcode + ";";

            try{
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();

                System.out.println(statement.executeUpdate(insertSupplier));
                System.out.println(statement.executeUpdate(updateGroStocks));

            }catch(SQLException ex){
                ex.printStackTrace();
            }catch(Exception ex){
                ex.printStackTrace();
            }

            //UPDATE UI
            selGrocery.setStocks(selGrocery.getStocks() + fieldSupQuant);
            tbvGroceries.refresh();

            txfSupName.clear();
            txfSupQuant.clear();
        }catch(BlankFieldException ex){
            Alert msg = new Alert(Alert.AlertType.WARNING);
            msg.setTitle("Warning");
            msg.setHeaderText("Blank space");
            msg.setContentText("A field is left unfilled. Please check again");
            msg.showAndWait();
        }catch(Exception ex){
            Alert msg = new Alert(Alert.AlertType.ERROR);
            msg.setTitle("ERROR");
            msg.setHeaderText("INPUT ERROR");
            msg.setContentText("An error has occured. Please check your fields again");
            msg.showAndWait();
        }
            
    }
    
    public String getDateToday(){
        String dateToday = null;
        
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        
        String yearToday = Integer.toString(year);
        String monthToday = to2CharString(month);
        String dayToday = to2CharString(day);
        
        dateToday = yearToday + "-" + monthToday + "-" +dayToday;
        System.out.println(dateToday);
        return dateToday;
    }
    
    public String to2CharString(int num){
        String converted = Integer.toString(num);
        
        if(num >= 0 && num <= 9){
            converted = "0" + num;
            return converted;
        }
        
        return converted;
    }
    
    public void clearFieldTable(ActionEvent e){
        clearGroTextFields();
        tbvGroceries.getSelectionModel().select(null);
        tbvGroceries.refresh();
    }
    
    public void clearGroTextFields(){
        txfGroBarcode.clear();
        txfGroName.clear();
        txfGroPrice.clear();
        txfGroCateg.clear();
        txfGroDesc.clear();
        
        txfSupBarcode.clear();
    }
    
}
