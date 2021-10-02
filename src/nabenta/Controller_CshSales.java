package nabenta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
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
public class Controller_CshSales implements Initializable{
    
    @FXML private ImageView imv_cashierPic;
    @FXML private Label lbl_cashierName;
    @FXML private Label lblTotalCust;
    @FXML private Label lblTotalOrd;
    @FXML private Label lblTotalSales;
    @FXML private Label lblTop1Name;
    @FXML private Label lblTop1Quant;
    @FXML private Label lblTop2Name;
    @FXML private Label lblTop2Quant;
    @FXML private Label lblTop3Name;
    @FXML private Label lblTop3Quant;
    @FXML private Label lblTop4Name;
    @FXML private Label lblTop4Quant;
    @FXML private Label lblTop5Name;
    @FXML private Label lblTop5Quant;
    @FXML private LineChart<String,Number> lchWeekly;
    @FXML private PieChart pchTopOrd;
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
        
        //Sales Report init
        
        //get the date today (YYYY-MM-DD)
        String dateToday = getDateToday();
        
        
        //From Database
        String dbUrl = "jdbc:mysql://localhost:3306/nabenta";
        String dbUsername = "root";
        String dbPassword = "souleater24";
        
        String totalCustomerQuery = "SELECT COUNT(*) 'Total' "
                + "FROM receipt "
                + "WHERE receipt.receipt_date >= '" + dateToday + " 00:00:00' AND receipt_date <= '" + dateToday + " 23:59:59';";
        
        String totalOrdersQuery = "SELECT COUNT(*) 'Total' "
                + "FROM orders "
                + "INNER JOIN receipt "
                + "ON receipt.receipt_id = orders.orders_id "
                + "WHERE receipt.receipt_date >= '" + dateToday + " 00:00:00' AND receipt_date <= '" + dateToday + " 23:59:59';";

        String totalSalesQuery = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + dateToday + " 00:00:00' AND receipt_date <= '" + dateToday + " 23:59:59';";
        
        String top5Query = "SELECT distinct(orders_barcode), grocery_itemName, SUM(orders_quantity) 'Total' "
                + "FROM orders "
                + "INNER JOIN receipt ON receipt.receipt_id = orders.orders_id "
                + "INNER JOIN grocery ON grocery.grocery_barcode = orders.orders_barcode "
                + "WHERE receipt_date >= '" + dateToday + " 00:00:00' AND receipt_date <= '" + dateToday + " 23:59:59' "
                + "GROUP BY orders_barcode "
                + "ORDER BY SUM(orders_quantity) DESC "
                + "LIMIT 5;";
        
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        
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
            
            result = statement.executeQuery(top5Query);
            if(result.isBeforeFirst()){
                
                String[] topNames = new String[5];
                Arrays.fill(topNames, "null");
                int[] topQuants = new int[5];
                
                for(int i = 0;result.next();i++){
                    topNames[i] = result.getString("grocery_itemName");
                    topQuants[i] = result.getInt("Total");
                }
                
                lblTop1Name.setText(topNames[0]);
                lblTop1Quant.setText(Integer.toString(topQuants[0]));
                pieData.add(new PieChart.Data(topNames[0], topQuants[0]));
                lblTop2Name.setText(topNames[1]);
                lblTop2Quant.setText(Integer.toString(topQuants[1]));
                pieData.add(new PieChart.Data(topNames[1], topQuants[1]));
                lblTop3Name.setText(topNames[2]);
                lblTop3Quant.setText(Integer.toString(topQuants[2]));
                pieData.add(new PieChart.Data(topNames[2], topQuants[2]));
                lblTop4Name.setText(topNames[3]);
                lblTop4Quant.setText(Integer.toString(topQuants[3]));
                pieData.add(new PieChart.Data(topNames[3], topQuants[3]));
                lblTop5Name.setText(topNames[4]);
                lblTop5Quant.setText(Integer.toString(topQuants[4]));
                pieData.add(new PieChart.Data(topNames[4], topQuants[4]));
                
                pchTopOrd.getData().addAll(pieData);
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        //Linechart
        
        //From Database
        String[] last7Days = getLast7Days();
        double[] last7DaysSales = new double[7];
        String day7Query = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last7Days[6] + " 00:00:00' AND receipt_date <= '" + last7Days[6] + " 23:59:59';";
        
        String day6Query = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last7Days[5] + " 00:00:00' AND receipt_date <= '" + last7Days[5] + " 23:59:59';";
        
        String day5Query = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last7Days[4] + " 00:00:00' AND receipt_date <= '" + last7Days[4] + " 23:59:59';";
        
        String day4Query = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last7Days[3] + " 00:00:00' AND receipt_date <= '" + last7Days[3] + " 23:59:59';";
        
        String day3Query = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last7Days[2] + " 00:00:00' AND receipt_date <= '" + last7Days[2] + " 23:59:59';";
        
        String day2Query = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last7Days[1] + " 00:00:00' AND receipt_date <= '" + last7Days[1] + " 23:59:59';";
        
        String day1Query = "SELECT SUM(receipt_total) 'Total' "
                + "FROM receipt "
                + "WHERE receipt_date >= '" + last7Days[0] + " 00:00:00' AND receipt_date <= '" + last7Days[0] + " 23:59:59';";
        
        try{
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(day7Query);
            result.next();
            last7DaysSales[0] = result.getDouble("Total");
            
            result = statement.executeQuery(day6Query);
            result.next();
            last7DaysSales[1] = result.getDouble("Total");
            
            result = statement.executeQuery(day5Query);
            result.next();
            last7DaysSales[2] = result.getDouble("Total");
            
            result = statement.executeQuery(day4Query);
            result.next();
            last7DaysSales[3] = result.getDouble("Total");
            
            result = statement.executeQuery(day3Query);
            result.next();
            last7DaysSales[4] = result.getDouble("Total");
            
            result = statement.executeQuery(day2Query);
            result.next();
            last7DaysSales[5] = result.getDouble("Total");
            
            result = statement.executeQuery(day1Query);
            result.next();
            last7DaysSales[6] = result.getDouble("Total");
            
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        lchWeekly.getData().clear();
        XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
        series.getData().add(new XYChart.Data<String,Number>("7 days", last7DaysSales[0]));
        series.getData().add(new XYChart.Data<String,Number>("6 days", last7DaysSales[1]));
        series.getData().add(new XYChart.Data<String,Number>("5 days", last7DaysSales[2]));
        series.getData().add(new XYChart.Data<String,Number>("4 days", last7DaysSales[3]));
        series.getData().add(new XYChart.Data<String,Number>("3 days", last7DaysSales[4]));
        series.getData().add(new XYChart.Data<String,Number>("Yesterday", last7DaysSales[5]));
        series.getData().add(new XYChart.Data<String,Number>("Today", last7DaysSales[6]));
        series.setName("Total Sales in a Day");
        lchWeekly.getData().add(series);
        
        //Piechart
        
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
    
    public String[] getLast7Days(){
        String[] last7Days = new String[7];
        
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        
        for(int i = 0;i < 7;i++){
            if(day != 1){
                String yearNow = Integer.toString(year);
                String monthNow = to2CharString(month);
                String dayNow = to2CharString(day - i);
                String date = yearNow + "-" + monthNow + "-" + dayNow;
                
                last7Days[i] = date;
            }else{
                calendar.add(Calendar.MONTH, - 1);
                
                String yearNow = Integer.toString(year);
                String monthNow = to2CharString(month);
                String dayNow = Integer.toString(calendar.getActualMaximum(Calendar.DATE));
                String date = yearNow + "-" + monthNow + "-" + dayNow;
                
                last7Days[i] = date;
                calendar.add(Calendar.MONTH, + 1);
            }
        }
        return last7Days;
    }
    
}
