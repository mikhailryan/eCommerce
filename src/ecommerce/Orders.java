package ecommerce; 

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Orders {
    Scanner scan = new Scanner(System.in);
    DecimalFormat df = new DecimalFormat("#.00");
    
    Config conf = new Config();
    Products prod = new Products();
    
    public void configOrders(){
        
        int opt;
        do {    
            try {
                System.out.println("\n\t Orders Menu \n");
                System.out.println("1. Place New Order");
                System.out.println("2. View Orders");
//                System.out.println("3. Cancel an Order");
                System.out.println("3. Go back..");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 
                
                System.out.println("");

                switch (opt) {
                    case 1:
                        System.out.println("\n\t\t=== Place New Order ===\n");
                        placeOrder();
                        break;
                        
                    case 2:
                        if (!conf.isTableEmpty("ORDERS")){
                            System.out.printf("\n%88s\n", "=== Orders List ===");
                            String query = "SELECT * FROM ORDERS";
                            viewOrders(query);
                            viewOrderDetails();
                        }else{
                            System.out.println("Orders Table is Empty.");
                        }
                        break;
                        
//                    case 3:
//                        if (!conf.isTableEmpty("ORDERS")){
//                            System.out.println("\n\t\t=== Cancel Order ===\n");
//                            cancelOrder();
//                        }else{
//                            System.out.println("Orders Table is Empty.");
//                        }
//                        break;
                        
                    case 3:
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
        } while (opt != 3);
    }
    
    public void placeOrder(){
        Customers cus = new Customers();
        
        
        int cusId;        
        boolean idExists;
        
        System.out.printf("\n%64s\n", "> CUSTOMERS LIST <");
        cus.viewCustomers("SELECT * FROM CUSTOMERS");
        
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
        ArrayList<Object> orderDetails = new ArrayList<>();
        
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
                
                int stocks = Integer.parseInt(conf.getDataFromID("PRODUCTS", productId, "p_stocks"));
                if(stocks <= 0){
                    System.out.println("Product ID: " + productId + " is currently out of stock.");
                    continue;
                }
                
                int qty = 0;
                boolean validQuantity = false;  
                while (!validQuantity || (stocks - qty) < 0) {
                    try {
                        System.out.print("Quantity: ");
                        qty = scan.nextInt();
                        validQuantity = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Please enter a valid number for quantity.");
                        scan.next();
                        validQuantity = false;
                    }
                    
                    if (validQuantity && (stocks - qty) < 0) {
                        System.out.println("Error: Only " + stocks + " units are available. Please enter a lower quantity.");
                        validQuantity = false; 
                    }
                }
                
                // Get them order details 
                double price = Double.parseDouble(conf.getDataFromID("PRODUCTS", productId, "p_price"));
                double lineTotal = price * qty;
                total += price * qty;
                orderDetails.add(new Object[]{productId, price, qty, lineTotal});
                
                // Deduct stocks in DB... idk what im doing..
                int updatedStocks = (stocks - qty);
                String[] columnNames = {"p_stocks"};
                String selectSql = "SELECT * FROM PRODUCTS WHERE ID = " + productId;
                String updStocksSql = "UPDATE PRODUCTS SET p_stocks = ? WHERE ID = ?";
                conf.updateRecord(updStocksSql, selectSql, columnNames, false, updatedStocks, productId);

            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number for quantity.");
                scan.next(); 
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid product ID.");
            } 
        } while (true);
        
        // Payment part...
        System.out.println("\nTotal Amount Due: " + df.format(total));
        double cash = 0;
        do {
            try {
                System.out.print("Enter Payment Cash: ");
                cash = scan.nextDouble();
                if (cash < total) {
                    System.out.println("Cash Insufficient.\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number for cash.\n");
                scan.next(); 
                cash = 0; 
            }
        } while (cash < total);
        double change = cash - total;
        
        
        String addOrderSql = "INSERT INTO ORDERS (customer_id, order_date, total_amount, cash, change) VALUES (?, ?, ?, ?, ?)";          
        conf.addRecord(addOrderSql, false, cusId, dateToday(), df.format(total), df.format(cash), df.format(change));    
        
        int orderId = conf.getID("SELECT * FROM ORDERS WHERE ROWID = (SELECT MAX(ROWID) FROM ORDERS);");
        String addDetailsSql = "INSERT INTO ORDERDETAILS (ID, prod_id, prod_price, quantity, line_total) VALUES (?, ?, ?, ?, ?)";
       
        orderDetails.stream().map((item) -> (Object[]) item).forEachOrdered((order) -> {
            conf.addRecord(addDetailsSql, false, orderId, (int)order[0], (double)order[1], (int)order[2], df.format((double)order[3]));
        });
        
        System.out.println("\nOrder Placed Successfully.");
    }
    
    public void viewOrders(String query){
        String[] Headers = {"Order ID", "Customer ID", "Order Date", "Total Amount", "Cash Paid", "Change"};
        String[] Columns = {"ID", "customer_id", "order_date", "total_amount", "cash", "change"};
        
        conf.viewRecords(query, Headers, Columns);
    }
    
    public void viewOrderDetails(){
        
        String resp;
        while(true){
            
            int id;
            boolean idExists;

            do{
                System.out.print("\nEnter ID to see order details ('Q' to go back): ");
                resp = scan.next();

                if(resp.equalsIgnoreCase("q")){
                    System.out.println("Going back...");
                    return;
                }
                
                id = 0;
                try {
                    id = Integer.parseInt(resp);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    return;
                }
                
                idExists = conf.doesIDExist("ORDERS", id);
                if(!idExists){
                    System.out.println("Order ID Doesn't Exist.");
                }
            }while(!idExists);

            int cusID = Integer.parseInt(conf.getDataFromID("ORDERS", id, "customer_id"));

            System.out.println("");
            System.out.println("Customer ID: " + conf.getDataFromID("CUSTOMERS", cusID, "ID"));
            System.out.println("Customer Name: " + conf.getDataFromID("CUSTOMERS", cusID, "name"));
            System.out.println("Total Amount: " + conf.getDataFromID("ORDERS", id, "total_amount"));

            System.out.println("---------------------------------------------------------------------------------------------------------");
            System.out.printf("\n%64s", "=== PRODUCTS ORDERED ===");
            System.out.printf("\n%61s\n", "=== ORDER ID " + id + " ===");
            String sql = "SELECT * FROM ORDERDETAILS WHERE id = " + id;
            String columnHeaders[] = {"Product ID", "Product Price", "Quantity", "Line Total"};
            String columnNames[] = {"prod_id", "prod_price", "quantity", "line_total"};

            conf.viewRecords(sql, columnHeaders, columnNames);
            System.out.println("\n---------------------------------------------------------------------------------------------------------");
            
        }
    }

    public void cancelOrder() {
        System.out.print("Order ID you want to Cancel: ");
        int id = scan.nextInt();
        
        conf.deleteRecord("ORDERS", id, true);
        conf.deleteRecord("ORDERDETAILS", id, false);
    }
    
    public String dateToday(){       
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy");       
        return LocalDateTime.now().format(format);
    }
}
