package nabenta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author SAFE
 */
public class Controller_AdmSales implements Initializable{
    
    @FXML private ImageView imv_cashierPic;
    @FXML private Label lbl_cashierName;
    @FXML private ChoiceBox cbxTimePeriod;
    @FXML private LineChart<String,Number> lchReport;
    @FXML private Label lblTotalCust;
    @FXML private Label lblTotalOrd;
    @FXML private Label lblTotalSales;
    @FXML private Label lblBestSel;
    @FXML private Label lblDateToday;
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
        
        //Time Period
        cbxTimePeriod.getItems().add("Weekly");
        cbxTimePeriod.getItems().add("Monthly");
        cbxTimePeriod.getItems().add("Yearly");
        cbxTimePeriod.getSelectionModel().select(0);
        
        cbxTimePeriod.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                reportSales();
            }
        });
        
        //Totals init
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryDateNow = "SELECT curDate() 'Date'; ";
        
        String totalCustomerQuery = "SELECT COUNT(*) 'Total' "
                + "FROM receipt ;";
        
        String totalOrdersQuery = "SELECT COUNT(*) 'Total' "
                + "FROM orders "
                + "INNER JOIN receipt "
                + "ON receipt.receipt_id = orders.orders_id;";

        String totalSalesQuery = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt ;";
        
        String top1Query = "SELECT distinct(orders_barcode), grocery_itemName, SUM(orders_quantity) 'Total' "
                + "FROM orders "
                + "INNER JOIN receipt ON receipt.receipt_id = orders.orders_id "
                + "INNER JOIN grocery ON grocery.grocery_barcode = orders.orders_barcode "
                + "GROUP BY orders_barcode "
                + "ORDER BY SUM(orders_quantity) DESC "
                + "LIMIT 1;";
        
        String dateNowQuery = "SELECT curDate() 'Date';";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(totalCustomerQuery);
            
            if(result.isBeforeFirst()){
                result.next();
                String totalCustomerServed = Integer.toString(result.getInt("Total"));
                lblTotalCust.setText(totalCustomerServed);
            }
            
            result = statement.executeQuery(totalOrdersQuery);
            if(result.isBeforeFirst()){
                result.next();
                String totalOrdersReceived = Integer.toString(result.getInt("Total"));
                lblTotalOrd.setText(totalOrdersReceived);
            }
            
            result = statement.executeQuery(totalSalesQuery);
            if(result.isBeforeFirst()){
                result.next();
                String totalSales = "₱ " + Double.toString(result.getDouble("Total"));
                lblTotalSales.setText(totalSales);
            }
            
            result = statement.executeQuery(top1Query);
            if(result.isBeforeFirst()){
                result.next();
                String bestSeller = result.getString("grocery_itemName");
                lblBestSel.setText(bestSeller);
            }
            
            result = statement.executeQuery(dateNowQuery);
            result.next();
            lblDateToday.setText(result.getDate("Date").toString());
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        //Report button
        reportSales();
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
    
    public void reportSales(){
        
        lchReport.getData().clear();
        
        String rptTimePeriod = cbxTimePeriod.getSelectionModel().getSelectedItem().toString();
        
        if(rptTimePeriod.equals("Weekly")){
            getWeeklyReport();
        }else if(rptTimePeriod.equals("Monthly")){
            getMonthlyReport();
        }else if(rptTimePeriod.equals("Yearly")){
            getYearlyReport();
        }
    }
    
    public void getWeeklyReport(){
        
        String last12Weeks[] = getLast12Weeks();
        double[] weeklySales = new double[12];
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String querySales = "";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = null;
            
            for(int i = 0;i < 12;i++){
                querySales = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last12Weeks[i+1] + " 00:00:00' AND receipt_date <= '" + last12Weeks[i] + " 23:59:59';";
                
                result = statement.executeQuery(querySales);
                result.next();
                weeklySales[i] = result.getDouble("Total");
            }
            
            System.out.println(Arrays.toString(weeklySales));
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        //Linechart
        lchReport.getData().clear();
        XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
        series.getData().add(new XYChart.Data<String,Number>("11 weeks ago", weeklySales[11]));
        series.getData().add(new XYChart.Data<String,Number>("10 weeks ago", weeklySales[10]));
        series.getData().add(new XYChart.Data<String,Number>("9 weeks ago", weeklySales[9]));
        series.getData().add(new XYChart.Data<String,Number>("8 weeks ago", weeklySales[8]));
        series.getData().add(new XYChart.Data<String,Number>("7 weeks ago", weeklySales[7]));
        series.getData().add(new XYChart.Data<String,Number>("6 weeks ago", weeklySales[6]));
        series.getData().add(new XYChart.Data<String,Number>("5 weeks ago", weeklySales[5]));
        series.getData().add(new XYChart.Data<String,Number>("4 weeks ago", weeklySales[4]));
        series.getData().add(new XYChart.Data<String,Number>("3 weeks ago", weeklySales[3]));
        series.getData().add(new XYChart.Data<String,Number>("2 weeks ago", weeklySales[2]));
        series.getData().add(new XYChart.Data<String,Number>("1 week ago", weeklySales[1]));
        series.getData().add(new XYChart.Data<String,Number>("THIS week", weeklySales[0]));
        series.setName("Total Sales per Week");
        lchReport.getData().add(series);
        
    }
    
    public String[] getLast12Weeks(){
        String[] last12Weeks = new String[13];
        String curDate = null;
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryDateNow = "SELECT curDate() 'Date'; ";
        String queryLastWeek = "";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(queryDateNow);
            
            result.next();
            curDate = result.getDate("Date").toString();
            last12Weeks[0] = curDate;
            
            queryLastWeek = "SELECT DATE_SUB('" + curDate + "', INTERVAL 7 DAY) 'Date';";
            
            for(int i = 1;i < 13;i++){
                result = statement.executeQuery(queryLastWeek);
                result.next();
                curDate = result.getDate("Date").toString();
                last12Weeks[i] = curDate;
                
                queryLastWeek = "SELECT DATE_SUB('" + curDate + "', INTERVAL 7 DAY) 'Date';";
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return last12Weeks;
    }
    
    public void getMonthlyReport(){
        
        String last12Months[] = getLast12Months();
        double[] monthlySales = new double[12];
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String querySales = "";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = null;
            
            for(int i = 0;i < 12;i++){
                querySales = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last12Months[i+1] + " 00:00:00' AND receipt_date <= '" + last12Months[i] + " 23:59:59';";
                
                result = statement.executeQuery(querySales);
                result.next();
                monthlySales[i] = result.getDouble("Total");
            }
            
            System.out.println(Arrays.toString(monthlySales));
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        //Linechart
        lchReport.getData().clear();
        XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
        series.getData().add(new XYChart.Data<String,Number>("11 months ago", monthlySales[11]));
        series.getData().add(new XYChart.Data<String,Number>("10 months ago", monthlySales[10]));
        series.getData().add(new XYChart.Data<String,Number>("9 months ago", monthlySales[9]));
        series.getData().add(new XYChart.Data<String,Number>("8 months ago", monthlySales[8]));
        series.getData().add(new XYChart.Data<String,Number>("7 months ago", monthlySales[7]));
        series.getData().add(new XYChart.Data<String,Number>("6 months ago", monthlySales[6]));
        series.getData().add(new XYChart.Data<String,Number>("5 months ago", monthlySales[5]));
        series.getData().add(new XYChart.Data<String,Number>("4 months ago", monthlySales[4]));
        series.getData().add(new XYChart.Data<String,Number>("3 months ago", monthlySales[3]));
        series.getData().add(new XYChart.Data<String,Number>("2 months ago", monthlySales[2]));
        series.getData().add(new XYChart.Data<String,Number>("1 month ago", monthlySales[1]));
        series.getData().add(new XYChart.Data<String,Number>("THIS month", monthlySales[0]));
        series.setName("Total Sales per Month");
        lchReport.getData().add(series);
        
    }
    
    public String[] getLast12Months(){
        String[] last12Months = new String[13];
        String curDate = null;
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryDateNow = "SELECT curDate() 'Date'; ";
        String queryLastWeek = "";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(queryDateNow);
            
            result.next();
            curDate = result.getDate("Date").toString();
            last12Months[0] = curDate;
            
            queryLastWeek = "SELECT DATE_SUB('" + curDate + "', INTERVAL 30 DAY) 'Date';";
            
            for(int i = 1;i < 13;i++){
                result = statement.executeQuery(queryLastWeek);
                result.next();
                curDate = result.getDate("Date").toString();
                last12Months[i] = curDate;
                
                queryLastWeek = "SELECT DATE_SUB('" + curDate + "', INTERVAL 7 DAY) 'Date';";
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return last12Months;
    }
    
    public void getYearlyReport(){
        
        String last12Years[] = getLast12Years();
        double[] yearlySales = new double[12];
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String querySales = "";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = null;
            
            for(int i = 0;i < 12;i++){
                querySales = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last12Years[i+1] + " 00:00:00' AND receipt_date <= '" + last12Years[i] + " 23:59:59';";
                
                result = statement.executeQuery(querySales);
                result.next();
                yearlySales[i] = result.getDouble("Total");
            }
            
            System.out.println(Arrays.toString(yearlySales));
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        //Linechart
        lchReport.getData().clear();
        XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
        series.getData().add(new XYChart.Data<String,Number>("11 years ago", yearlySales[11]));
        series.getData().add(new XYChart.Data<String,Number>("10 years ago", yearlySales[10]));
        series.getData().add(new XYChart.Data<String,Number>("9 years ago", yearlySales[9]));
        series.getData().add(new XYChart.Data<String,Number>("8 years ago", yearlySales[8]));
        series.getData().add(new XYChart.Data<String,Number>("7 years ago", yearlySales[7]));
        series.getData().add(new XYChart.Data<String,Number>("6 years ago", yearlySales[6]));
        series.getData().add(new XYChart.Data<String,Number>("5 years ago", yearlySales[5]));
        series.getData().add(new XYChart.Data<String,Number>("4 years ago", yearlySales[4]));
        series.getData().add(new XYChart.Data<String,Number>("3 years ago", yearlySales[3]));
        series.getData().add(new XYChart.Data<String,Number>("2 years ago", yearlySales[2]));
        series.getData().add(new XYChart.Data<String,Number>("1 year ago", yearlySales[1]));
        series.getData().add(new XYChart.Data<String,Number>("THIS year", yearlySales[0]));
        series.setName("Total Sales per Year");
        lchReport.getData().add(series);
        
    }
    
    public String[] getLast12Years(){
        String[] last12Years = new String[13];
        String curDate = null;
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        String queryDateNow = "SELECT curDate() 'Date'; ";
        String queryLastWeek = "";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(queryDateNow);
            
            result.next();
            curDate = result.getDate("Date").toString();
            last12Years[0] = curDate;
            
            queryLastWeek = "SELECT DATE_SUB('" + curDate + "', INTERVAL 365 DAY) 'Date';";
            
            for(int i = 1;i < 13;i++){
                result = statement.executeQuery(queryLastWeek);
                result.next();
                curDate = result.getDate("Date").toString();
                last12Years[i] = curDate;
                
                queryLastWeek = "SELECT DATE_SUB('" + curDate + "', INTERVAL 7 DAY) 'Date';";
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return last12Years;
    }
    
    public String to2CharString(int num){
        String converted = Integer.toString(num);
        
        if(num >= 0 && num <= 9){
            converted = "0" + num;
            return converted;
        }
        
        return converted;
    }
    
}
