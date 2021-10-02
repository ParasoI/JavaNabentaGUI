package nabenta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class Controller_AdmReceipt implements Initializable{
    
    @FXML private ImageView imv_cashierPic;
    @FXML private Label lbl_cashierName;
    @FXML private TableView tbvReceipts;
    @FXML private TableColumn tbcId;
    @FXML private TableColumn tbcDate;
    @FXML private TableColumn tbcDiscount;
    @FXML private TableColumn tbcTotal;
    @FXML private TableColumn tbcCashTen;
    @FXML private TextField txfId;
    private ObservableList receiptData = FXCollections.observableArrayList();
    @FXML private TableView tbvOrders;
    @FXML private TableColumn tbcOrdId;
    @FXML private TableColumn tbcOrdName;
    @FXML private TableColumn tbcOrdQuant;
    private ObservableList ordersData = FXCollections.observableArrayList();
    private ObservableList oldOrdersData = FXCollections.observableArrayList(ordersData);
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
        
        //Table Receipt and Order init
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryReceipt = "SELECT receipt_id, receipt_date, receipt_discount, receipt_total, receipt_cashTendered "
                + "FROM receipt ;";
        
        String queryOrders = "SELECT orders_id, orders_barcode, grocery.grocery_itemName, orders_quantity "
                + "FROM orders "
                + "INNER JOIN grocery "
                + "ON grocery.grocery_barcode = orders.orders_barcode "
                + "WHERE orders_barcode = grocery.grocery_barcode;";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(queryOrders);
            
            //Orders
            while (result.next()) {                
                ordersData.add(new Model_OrderRec(
                        result.getLong("orders_id"),
                        result.getLong("orders_barcode"),
                        result.getString("grocery_itemName"),
                        result.getInt("orders_quantity")
                    ));
            }
            
            tbcOrdId.setCellValueFactory(new PropertyValueFactory("id"));
            tbcOrdName.setCellValueFactory(new PropertyValueFactory("itemName"));
            tbcOrdQuant.setCellValueFactory(new PropertyValueFactory("quantity"));
            //tbvOrders.setItems(ordersData);
            oldOrdersData = FXCollections.observableArrayList(ordersData);
            
            //Receipts
            result = statement.executeQuery(queryReceipt);
            
            while (result.next()) {                
                receiptData.add(new Model_Receipt(
                        result.getLong("receipt_id"),
                        result.getString("receipt_date"),
                        result.getDouble("receipt_discount"),
                        result.getDouble("receipt_total"),
                        result.getInt("receipt_cashTendered")
                    ));
            }
            
            tbcId.setCellValueFactory(new PropertyValueFactory("id"));
            tbcDate.setCellValueFactory(new PropertyValueFactory("date"));
            tbcDiscount.setCellValueFactory(new PropertyValueFactory("discount"));
            tbcTotal.setCellValueFactory(new PropertyValueFactory("total"));
            tbcCashTen.setCellValueFactory(new PropertyValueFactory("cashTendered"));
            tbvReceipts.setItems(receiptData);
            
            tbvReceipts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    try{
                        Model_Receipt selRow = (Model_Receipt) tbvReceipts.getSelectionModel().getSelectedItem();
                        if(selRow != null){
                            txfId.setText(String.valueOf(selRow.getId()));
                            
                            ordersData = FXCollections.observableArrayList();
                            
                            Model_OrderRec curOrder;
                            for(int i = 0;i < oldOrdersData.size();i++){
                                curOrder = (Model_OrderRec)oldOrdersData.get(i);
                                
                                if(curOrder.getId() == selRow.getId()){
                                    ordersData.add(curOrder);
                                }
                            }
                            
                            tbvOrders.getItems().clear();
                            tbvOrders.getItems().addAll(ordersData);
                            tbvOrders.refresh();
                        }else{
                            tbvOrders.getItems().clear();
                            tbvOrders.refresh();
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
        
        //Textfield properties
        txfId.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                Long query = 0l;
                try{
                    query = Long.parseLong(txfId.getText());
                }catch(Exception e){
                    
                }
                
                Model_Receipt oldGrocery = null;
                for(int i = 0;i < tbvReceipts.getItems().size();i++){
                    oldGrocery = (Model_Receipt)tbvReceipts.getItems().get(i);
                    if(query == oldGrocery.getId()){
                        tbvReceipts.getSelectionModel().select(i);
                        break;
                    }else{
                        tbvReceipts.getSelectionModel().select(null);
                    }
                }
            }
        });
        
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
    
    public void deleteReceipt(ActionEvent e){
        try{
            Long fieldId = Long.parseLong(txfId.getText());
            Model_Receipt selReceipt = (Model_Receipt)tbvReceipts.getSelectionModel().getSelectedItem();
        
            if(fieldId == selReceipt.getId()){
                //From Database
                String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
                String dbUsername = "root";
                String dbPassword = "souleater24";
                String deleteOrder = "DELETE FROM orders "
                        + "WHERE orders_id = " + fieldId + ";";
                
                String deleteReceipt = "DELETE FROM receipt "
                        + "WHERE receipt_id = " + fieldId + ";";
                
                try{
                    Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                    Statement statement = connection.createStatement();
                    
                    System.out.println(statement.executeUpdate(deleteOrder));
                    System.out.println(statement.executeUpdate(deleteReceipt));

                }catch(SQLException ex){
                    ex.printStackTrace();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
                //Update UI
                Model_OrderRec curOrder;
                for(int i = 0;i < oldOrdersData.size();i++){
                    curOrder = (Model_OrderRec)oldOrdersData.get(i);
                    if(selReceipt.getId() == curOrder.getId()){
                        oldOrdersData.remove(i);
                    }
                }
                
                tbvReceipts.getItems().remove(selReceipt);
                tbvReceipts.getSelectionModel().select(null);
                tbvReceipts.refresh();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
