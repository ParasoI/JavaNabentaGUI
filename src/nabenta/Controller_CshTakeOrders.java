package nabenta;


import exceptions.InvalidReceiptDetailsException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

public class Controller_CshTakeOrders implements Initializable{
    
    @FXML private ImageView imv_cashierPic;
    @FXML private Label lbl_cashierName;
    @FXML private TableView tbvGroceries;
    @FXML private TableColumn tbcBarcode;
    @FXML private TableColumn tbcName;
    @FXML private TableColumn tbcPrice;
    @FXML private TableColumn tbcStocks;
    @FXML private TextField txfBarcode;
    @FXML private TableView tbvOrders;
    @FXML private TableColumn tbcOrdName;
    @FXML private TableColumn tbcOrdQuantity;
    @FXML private TableColumn tbcOrdPrice;
    @FXML private Label lblTotal;
    @FXML private RadioButton rbt0Dis;
    @FXML private RadioButton rbt2Dis;
    @FXML private RadioButton rbt5Dis;
    @FXML private RadioButton rbt8Dis;
    @FXML private RadioButton rbt10Dis;
    @FXML private RadioButton rbt12Dis;
    @FXML private RadioButton rbt15Dis;
    @FXML private RadioButton rbt20Dis;
    @FXML private RadioButton rbt25Dis;
    @FXML private TextField txfCashTendered;
    @FXML private Label lblChange;
    private ObservableList groceryData = FXCollections.observableArrayList();
    private ObservableList orderData = FXCollections.observableArrayList();
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryGroceries = "SELECT grocery_barcode, grocery_itemName, grocery_price, grocery_stocksLeft "
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
                        result.getInt("grocery_stocksLeft")
                    ));
            }
            
            tbcBarcode.setCellValueFactory(new PropertyValueFactory("barcode"));
            tbcName.setCellValueFactory(new PropertyValueFactory("name"));
            tbcPrice.setCellValueFactory(new PropertyValueFactory("price"));
            tbcStocks.setCellValueFactory(new PropertyValueFactory("stocks"));
            tbvGroceries.setItems(groceryData);
            
            tbvGroceries.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    try{
                        Model_Grocery selRow = (Model_Grocery) tbvGroceries.getSelectionModel().getSelectedItem();
                        if(selRow != null){
                            txfBarcode.setText(String.valueOf(selRow.getBarcode()));
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
        
        //Barcode TextField init
        txfBarcode.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                Long query = 0l;
                try{
                    query = Long.parseLong(txfBarcode.getText());
                }catch(Exception e){
                    
                }
                
                Model_Grocery oldGrocery = null;
                for(int i = 0;i < tbvGroceries.getItems().size();i++){
                    oldGrocery = (Model_Grocery)tbvGroceries.getItems().get(i);
                    if(query == oldGrocery.getBarcode()){
                        tbvGroceries.getSelectionModel().select(i);
                        break;
                    }
                }
            }
        });
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
    
    public void addToOrders(ActionEvent e){
        if(!txfBarcode.getText().equals("")){
            //From Database
            String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
            String dbUsername = "root";
            String dbPassword = "souleater24";
            String queryOrder = "SELECT grocery_barcode, grocery_itemName, grocery_price, grocery_stocksLeft "
                    + "FROM grocery "
                    + "WHERE grocery_barcode = " + txfBarcode.getText() + ";";

            try{
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(queryOrder);

                //ORDERS
                if(result.isBeforeFirst()){
                    result.next();
                    
                    Model_Grocery curGrocery = null;
                    for(int i = 0;i < tbvGroceries.getItems().size();i++){
                        curGrocery = (Model_Grocery)tbvGroceries.getItems().get(i);
                        if(curGrocery.getBarcode() == result.getLong("grocery_barcode")){
                            break;
                        }
                    }
                    if(result.getInt("grocery_stocksLeft") <= 0 || curGrocery.getStocks() <= 0){
                        Alert message = new Alert(Alert.AlertType.INFORMATION);
                        message.setTitle("FYI");
                        message.setHeaderText("Empty stocks");
                        message.setContentText("Your order can't be added due to insufficient stocks. Please order from suppliers.");
                        message.showAndWait();
                    }else{
                        Model_Orders newOrder = new Model_Orders(
                            result.getLong("grocery_barcode"),
                            result.getString("grocery_itemName"),
                            1,
                            result.getInt("grocery_price"),
                            result.getInt("grocery_price")
                        );

                        Model_Orders oldOrder = null;
                        boolean isDuplicate = false;
                         for(int i = 0;i < tbvOrders.getItems().size();i++){
                            oldOrder = (Model_Orders)tbvOrders.getItems().get(i);
                            if(newOrder.getBarcode() == oldOrder.getBarcode()){
                                isDuplicate = true;
                                break;
                            }else{
                                isDuplicate = false;
                            }
                        }

                        if(isDuplicate){
                            updateGroceryUI(oldOrder);
                            oldOrder.setQuantity(oldOrder.getQuantity() + 1);
                            oldOrder.setPrice(oldOrder.getGroceryPrice() * oldOrder.getQuantity());
                            tbvOrders.getSelectionModel().select(tbvOrders.getItems().indexOf(oldOrder));
                        }else{
                            updateGroceryUI(newOrder);
                            orderData.add(newOrder);
                            if(tbvOrders.getItems().size() == 0){
                                tbvOrders.getSelectionModel().select(newOrder);
                            }else{
                                tbvOrders.getSelectionModel().select(tbvOrders.getItems().size() - 1);
                            }
                        }
                        tbvOrders.refresh();

                        updateTotal(newOrder);

                        //Play Beep Sounds
                        AudioClip beep = new AudioClip(getClass().getResource("assets/sounds/beep.mp3").toExternalForm());
                        beep.play();
                        
                        txfBarcode.clear();
                        tbvGroceries.getSelectionModel().select(null);
                    }
                }else{
                    //custom EXCEPTION
                }

                tbcOrdName.setCellValueFactory(new PropertyValueFactory("name"));
                tbcOrdQuantity.setCellValueFactory(new PropertyValueFactory("quantity"));
                tbcOrdPrice.setCellValueFactory(new PropertyValueFactory("price"));
                tbvOrders.setItems(orderData);

            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        
    }

    public void deleteFromOrders(ActionEvent e){
        if(tbvOrders.getSelectionModel().getSelectedIndex() >= 0){
            int index = tbvOrders.getSelectionModel().getSelectedIndex();

            //Return Stocks to UI
            Model_Orders curOrder = (Model_Orders) tbvOrders.getItems().get(index);
            Model_Grocery grocery = null;
            for(int i = 0; i < tbvGroceries.getItems().size();i++){
                grocery = (Model_Grocery)tbvGroceries.getItems().get(i);
                if(grocery.getBarcode() == curOrder.getBarcode()){
                    grocery.setStocks(grocery.getStocks() + curOrder.getQuantity());
                    tbvGroceries.refresh();
                    break;
                }
            }

            tbvOrders.getItems().remove(index);
            updateTotal(e);
        }else{
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setHeaderText("Invalid Order");
            message.setContentText("Please select a row to be removed");
            message.showAndWait();
        }
        
    }
    
    public void voidOrder(ActionEvent e){
        //Return Stocks to UI
        Model_Orders curOrder = null;
        Model_Grocery curGrocery = null;
        for(int i = 0;i < tbvOrders.getItems().size();i++){
            curOrder = (Model_Orders)tbvOrders.getItems().get(i);
            for(int j = 0;j < tbvGroceries.getItems().size();j++){
                curGrocery = (Model_Grocery)tbvGroceries.getItems().get(j);
                if(curOrder.getBarcode() == curGrocery.getBarcode()){
                    curGrocery.setStocks(curGrocery.getStocks() + curOrder.getQuantity());
                    tbvGroceries.refresh();
                    break;
                }
            }
        }
        
        tbvOrders.getItems().clear();
        txfBarcode.clear();
        tbvGroceries.getSelectionModel().select(null);
        txfCashTendered.clear();
        rbt0Dis.setSelected(true);
        updateTotal(e);
    }
    
    public void updateGroceryUI(Model_Orders order){
        Model_Grocery grocery = null;
        for(int i = 0;i < tbvGroceries.getItems().size();i++){
            grocery = (Model_Grocery)tbvGroceries.getItems().get(i);
            if(order.getBarcode() == grocery.getBarcode()){
                grocery.setStocks(grocery.getStocks() - 1);
                tbvGroceries.refresh();
                break;
            }
        }
    }
    
    public void updateTotal(Model_Orders newOrder){
        int subtotal = 0;
        double discount = 0.0;
        
        if(tbvOrders.getItems().size() == 0){
            subtotal += newOrder.getPrice();
        }else{
            for(int i = 0;i < tbvOrders.getItems().size();i++){
                Model_Orders currentOrder = (Model_Orders)tbvOrders.getItems().get(i);
                subtotal += currentOrder.getPrice();
            }
        }
        
        if(rbt0Dis.isSelected()){
            discount = 0.0;
        }else if(rbt2Dis.isSelected()){
            discount = 0.02;
        }else if(rbt5Dis.isSelected()){
            discount = 0.05;
        }else if(rbt8Dis.isSelected()){
            discount = 0.08;
        }else if(rbt10Dis.isSelected()){
            discount = 0.10;
        }else if(rbt12Dis.isSelected()){
            discount = 0.12;
        }else if(rbt15Dis.isSelected()){
            discount = 0.15;
        }else if(rbt20Dis.isSelected()){
            discount = 0.20;
        }else if(rbt25Dis.isSelected()){
            discount = 0.25;
        }else{
            discount = 0.0;
        }
        
        double total = subtotal - (subtotal * discount);
        total = Math.round(total * 100.0) / 100.0;
        
        
        lblTotal.setText("Total: ₱" + total);
    }
    
    public void updateTotal(ActionEvent e){
        int subtotal = 0;
        double discount = 0.0;
        
        try{
            for(int i = 0;i < tbvOrders.getItems().size();i++){
                Model_Orders currentOrder = (Model_Orders)tbvOrders.getItems().get(i);
                subtotal += currentOrder.getPrice();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        if(rbt0Dis.isSelected()){
            discount = 0.0;
        }else if(rbt2Dis.isSelected()){
            discount = 0.02;
        }else if(rbt5Dis.isSelected()){
            discount = 0.05;
        }else if(rbt8Dis.isSelected()){
            discount = 0.08;
        }else if(rbt10Dis.isSelected()){
            discount = 0.10;
        }else if(rbt12Dis.isSelected()){
            discount = 0.12;
        }else if(rbt15Dis.isSelected()){
            discount = 0.15;
        }else if(rbt20Dis.isSelected()){
            discount = 0.20;
        }else if(rbt25Dis.isSelected()){
            discount = 0.25;
        }else{
            discount = 0.0;
        }
        
        double total = subtotal - (subtotal * discount);
        total = Math.round(total * 100.0) / 100.0;
        
        lblTotal.setText("Total: ₱" + total);
    }
    
    public void processReceipt(ActionEvent e){
        
        int amountReceived = 0;
        double total = 0;
        
        try{
            amountReceived = Integer.parseInt(txfCashTendered.getText());
            String totalStr = lblTotal.getText();
            totalStr = totalStr.substring(totalStr.indexOf('₱') + 1, totalStr.length());
            total = Double.parseDouble(totalStr);
            
            if(total > 0.0){
                long receiptCreated = createReceipt(amountReceived,total);
                insertOrders(receiptCreated);
                updateChange(receiptCreated);
                
                //Update grocery_stocksLeft
                reduceStocksToDB();
                
                //Reset UI
                tbvOrders.getItems().clear();
                txfBarcode.clear();
                tbvGroceries.getSelectionModel().select(null);
                txfCashTendered.clear();
                rbt0Dis.setSelected(true);
                updateTotal(e);
            }else{
                throw new InvalidReceiptDetailsException();
            }
            
        }catch(NumberFormatException ex){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setHeaderText("Invalid Number");
            message.setContentText("Please enter only whole numbers. Try again");
            message.showAndWait();
        }catch(InvalidReceiptDetailsException ex){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setHeaderText("Invalid Order Details.");
            message.setContentText("Please add atleast one order to the list");
            message.showAndWait();
        }catch(Exception ex){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setHeaderText("Invalid number");
            message.setContentText("Please enter only whole numbers. Try again");
            message.showAndWait();
            
            txfCashTendered.clear();
            ex.printStackTrace();
        }
    }
    
    public Long createReceipt(int amountReceived, double total){
         //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String insertReceipt = "";
        
        Long receiptId = createReceiptID();
        Timestamp receiptDate = createReceiptDate();
        double receiptDiscount = createReceiptDiscount();
        double receiptTotal = createReceiptTotal();
        int receiptCashTendered = Integer.parseInt(txfCashTendered.getText());
        int receiptCashier = Model_Employee.getId();
        
        insertReceipt = "INSERT INTO receipt VALUES("
                + receiptId + ", "
                + "'" + receiptDate + "'" + ", "
                + receiptDiscount + ", "
                + receiptTotal + ", "
                + receiptCashTendered + ", "
                + receiptCashier + ");";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            System.out.println(statement.executeUpdate(insertReceipt));
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return receiptId;
    }
    
    public Long createReceiptID(){
        Long receiptID = 0l;
         //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String yearQuery = "SELECT YEAR(NOW());"; 
        String monthQuery = "SELECT MONTH(NOW());"; 
        String dayQuery = "SELECT DAY(NOW());"; 
        String hourQuery = "SELECT HOUR(NOW());"; 
        String minuteQuery = "SELECT MINUTE(NOW());"; 
        String secondQuery = "SELECT second(NOW());"; 
        
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(yearQuery);
            result.next();
            String year = Integer.toString(result.getInt("YEAR(NOW())"));
            result = statement.executeQuery(monthQuery);
            result.next();
            String month = Integer.toString(result.getInt("MONTH(NOW())"));
            month = to2CharString(month);
            result = statement.executeQuery(dayQuery);
            result.next();
            String day = Integer.toString(result.getInt("DAY(NOW())"));
            day = to2CharString(day);
            result = statement.executeQuery(hourQuery);
            result.next();
            String hour = Integer.toString(result.getInt("HOUR(NOW())"));
            hour = to2CharString(hour);
            result = statement.executeQuery(minuteQuery);
            result.next();
            String minute = Integer.toString(result.getInt("MINUTE(NOW())"));
            minute = to2CharString(minute);
            result = statement.executeQuery(secondQuery);
            result.next();
            String second = Integer.toString(result.getInt("SECOND(NOW())"));
            
            String id = year + ""
                + month + ""
                + day + ""
                + hour + ""
                + minute + ""
                + second;
            
            receiptID = Long.parseLong(id);
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return receiptID;
    }
    
    public Timestamp createReceiptDate(){
        Timestamp receiptDate = null;
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryDate = "SELECT NOW();";
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(queryDate);
            
            result.next();
            receiptDate = result.getTimestamp("NOW()");
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return receiptDate;
    }
    
    public String to2CharString(String str){
        String converted = str;
        
        int num = Integer.parseInt(str);
        
        if(num >= 0 && num <= 9){
            converted = 0 + str;
            return converted;
        }
        
        return converted;
    }
    
    public double createReceiptDiscount(){
        double discount = 0.0;
        
        if(rbt0Dis.isSelected()){
            discount = 0.0;
        }else if(rbt2Dis.isSelected()){
            discount = 0.02;
        }else if(rbt5Dis.isSelected()){
            discount = 0.05;
        }else if(rbt8Dis.isSelected()){
            discount = 0.08;
        }else if(rbt10Dis.isSelected()){
            discount = 0.10;
        }else if(rbt12Dis.isSelected()){
            discount = 0.12;
        }else if(rbt15Dis.isSelected()){
            discount = 0.15;
        }else if(rbt20Dis.isSelected()){
            discount = 0.20;
        }else if(rbt25Dis.isSelected()){
            discount = 0.25;
        }else{
            discount = 0.0;
        }
        
        return discount;
    }
    
    public double createReceiptTotal(){
        double total = 0.0;
        
        try{
            String totalStr = lblTotal.getText();
            totalStr = totalStr.substring(totalStr.indexOf('₱') + 1, totalStr.length());
            total = Double.parseDouble(totalStr);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return total;
    }
    
    public void insertOrders(Long receiptId){
        for(int i = 0;i < tbvOrders.getItems().size();i++){
            Model_Orders curOrders = (Model_Orders)tbvOrders.getItems().get(i);
            
             //From Database
            String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
            String dbUsername = "root";
            String dbPassword = "souleater24";
            String insertOrder = "";

            Long orderId = receiptId;
            Long orderBarcode = curOrders.getBarcode();
            int orderQuantity = curOrders.getQuantity();

            insertOrder = "INSERT INTO orders VALUES("
                    + orderId + ", "
                    + orderBarcode + ", "
                    + orderQuantity + ");";

            try{
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                System.out.println(statement.executeUpdate(insertOrder));
            }catch(SQLException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void reduceStocksToDB(){
        Model_Orders curOrder = null;
        for(int i = 0;i < tbvOrders.getItems().size();i++){
            curOrder = (Model_Orders)tbvOrders.getItems().get(i);
            
            //From Database
            String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
            String dbUsername = "root";
            String dbPassword = "souleater24";
            String reduceStocks = "";
            
            Long barcode = curOrder.getBarcode();
            int quantityReduce = curOrder.getQuantity();


            reduceStocks = "UPDATE grocery SET "
                    + "grocery_stocksLeft = grocery_stocksLeft - " + quantityReduce + " "
                    + "WHERE grocery_barcode = " + barcode;

            try{
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                System.out.println(statement.executeUpdate(reduceStocks));
            }catch(SQLException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void updateChange(Long receiptId){
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String changeQuery = "";
        
        changeQuery = "SELECT receipt_total, receipt_cashTendered "
                + "FROM receipt "
                + "WHERE receipt_id = " + receiptId + ";";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(changeQuery);
            
            result.next();
            double change = result.getInt("receipt_cashTendered") - result.getDouble("receipt_total");
            change = Math.round(change * 100.0) / 100.0;
            
            lblChange.setText("Change = ₱" + change);
            
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}