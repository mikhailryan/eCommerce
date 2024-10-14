package ecommerce;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Orders {
    Scanner scan = new Scanner(System.in);
    Config conf = new Config();
    
    public void configOrders(){
        int opt;

        do {    
            try {
                System.out.println("\n\t=== Orders Menu ===\n");
                System.out.println("1. Place New Order");
                System.out.println("2. View Orders");
                System.out.println("3. Cancel an Order");
                System.out.println("4. Update an Existing Order");
                System.out.println("5. Go back..");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 

                switch (opt) {
                    case 1:
                        System.out.println("\n\t\t=== Place New Order ===\n");
                        placeOrder();
                        break;
                    case 2:
                        System.out.println("\n\t\t\t\t     === Orders List ===\n");
                        String query = "SELECT * FROM ORDERS";
                        viewOrders(query);
                        viewOrderDetails();
                        break;
                    case 3:
                        System.out.println("\n\t\t=== Cancel Order ===\n");
                        cancelOrder();
                        break;
                    case 4:
                        System.out.println("\n\t\t=== Update Order ===\n");
                        updateOrder();
                        break;
                    case 5:
                        System.out.println("\nGoing back to Main Menu...");
                        System.out.println("------------------------------------------------------------------");         
                        break;
                    default:
                        System.out.println("Invalid Option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scan.nextLine(); 
                opt = -1; 
            }
        } while (opt != 5);
    }
    
    public void placeOrder(){
        
        int cusId;        
        boolean idExists;
        do{
            System.out.print("Customer ID: ");
            cusId = scan.nextInt();
            
            idExists = conf.doesIDExist("CUSTOMERS", cusId);
            if(!idExists){
                System.out.println("Customer ID Doesn't Exist.\n");
            }
        }while(!idExists);
        scan.nextLine();
        
        System.out.println("\nAvailable Products:");
        Products pr = new Products();
        pr.viewProducts("SELECT * FROM PRODUCTS");
        
        String resp;
        double total = 0;
        ArrayList<int[]> orderDetails = new ArrayList<>();
         
        do {
            try {
                resp = "";
                System.out.print("\nID of Ordered Product (or type 'done' to finish): ");
                resp = scan.next();
                
                if (resp.equalsIgnoreCase("done")) {
                    break;
                }
                int productId = Integer.parseInt(resp);
                if (!conf.doesIDExist("PRODUCTS", productId)){
                    System.out.println("Product ID doesn't exist.");
                    continue;
                }
                
                double price = Double.parseDouble(conf.getDataFromID("PRODUCTS", productId, "p_price"));
                
                int qty = 0;
                boolean validQuantity = false;  
                while (!validQuantity) {
                    try {
                        System.out.print("Quantity: ");
                        qty = scan.nextInt();
                        validQuantity = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Please enter a valid number for quantity.");
                        scan.next();
                    }
                }
                
                total += price * qty;
                orderDetails.add(new int[]{productId, qty});

            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number for quantity.");
                scan.next(); 
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid product ID.");
            } 
        } while (true);
      
        String addOrderSql = "INSERT INTO ORDERS (customer_id, order_date, total_amount) VALUES (?, ?, ?)";          
        conf.addRecord(addOrderSql, false, cusId, dateToday(), total);    
        
        int orderId = conf.getID("SELECT * FROM ORDERS WHERE ROWID = (SELECT MAX(ROWID) FROM ORDERS);");
        String addDetailsSql = "INSERT INTO ORDERDETAILS (ID, prod_id, quantity) VALUES (?, ?, ?)";
        orderDetails.forEach((order) -> {
            conf.addRecord(addDetailsSql, false, orderId, order[0], order[1]);
        });
        
        System.out.println("\nOrder Placed Successfully.");
    }
    
    public void viewOrders(String query){
        String[] Headers = {"Order ID", "Customer ID", "Order Date", "Total Amount"};
        String[] Columns = {"ID", "customer_id", "order_date", "total_amount"};
        
        conf.viewRecords(query, Headers, Columns);
    }
    
    public void viewOrderDetails(){
        
        String resp;
        int id;
        boolean idExists;
        
        do{
            System.out.print("\nEnter ID to see order details ('back' to go back): ");
            resp = scan.next();
            
            if(resp.equalsIgnoreCase("back")){
                System.out.println("Going back...");
                return;
            }
            
            id = Integer.parseInt(resp);
            
            idExists = conf.doesIDExist("ORDERS", id);
            if(!idExists){
                System.out.println("Order ID Doesn't Exist.");
            }
        }while(!idExists);
        
        int cusID = Integer.parseInt(conf.getDataFromID("ORDERS", id, "customer_id"));
        
        System.out.println("Customer Name: " + conf.getDataFromID("CUSTOMERS", cusID, "name"));
        System.out.println("Email: " + conf.getDataFromID("CUSTOMERS", cusID, "email"));
        System.out.println("Address: " + conf.getDataFromID("CUSTOMERS", cusID, "address") + "\n");
        
        String sql = "SELECT * FROM ORDERDETAILS WHERE id = " + id;
        String columnHeaders[] = {"Product ID", "Quantity"};
        String columnNames[] = {"prod_id", "quantity"};
        conf.viewRecords(sql, columnHeaders, columnNames);
    }
    
    public String dateToday(){       
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy");       
        return LocalDate.now().format(format);
    }

    private void cancelOrder() {
        System.out.print("Order ID you want to Cancel: ");
        int id = scan.nextInt();
        
        conf.deleteRecord("ORDERS", id, true);
        conf.deleteRecord("ORDERDETAILS", id, false);
    }
 
    private void updateOrder() {
        
    }
}
