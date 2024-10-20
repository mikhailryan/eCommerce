package ecommerce;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Reports {
    Scanner scan = new Scanner(System.in);
    Config conf = new Config();
    Customers cus = new Customers();
    
    public void generateReports(){
        int opt;
        do {    
            try {
                System.out.println("\n\t=== Reports ===\n");
                System.out.println("1. Customer Purchased History Report");
                System.out.println("2. Inventory Report");
                System.out.println("3. Sales Report");
                System.out.println("4. Go back..");

                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 

                switch (opt) {
                    case 1:  
                        customerPurchasedHistory();
                        break;

                    case 2:              
                        System.out.printf("\n%64s\n", "=== INVENTORY REPORT ===");
                        break;

                    case 3:
                        System.out.printf("\n%63s\n", "=== SALES REPORT ===");
                        break;

                    case 4:
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
        } while (opt != 4);

    }

    private void customerPurchasedHistory() {
        
        System.out.printf("\n%64s\n\n", "=== CUSTOMERS LIST ===");
        cus.viewCustomers("SELECT * FROM CUSTOMERS");
        
        int cusID;
        do {
            System.out.print("Enter Customer ID: ");
            cusID = scan.nextInt();
            if (!conf.doesIDExist("CUSTOMERS", cusID)) {
                System.out.println("Customer ID Doesn't Exist.\n");
            }
        } while (!conf.doesIDExist("CUSTOMERS", cusID));
        scan.nextLine();
        
        System.out.printf("\n%83s", "=== CUSTOMER PURCHASED HISTORY ===");
        System.out.println("\n\n===================================================================================================================================");
        System.out.println("\t\t\t\t\t\t     Customer: " + conf.getDataFromID("CUSTOMERS", cusID, "name"));
        System.out.println("\t\t\t\t\t\t     Email: " + conf.getDataFromID("CUSTOMERS", cusID, "email"));
        System.out.println("\t\t\t\t\t\t     Address: " + conf.getDataFromID("CUSTOMERS", cusID, "address"));
        System.out.println("===================================================================================================================================\n");
        
        List<Integer> orderIDs = conf.getOrderIdsByCustomerId(cusID);
        double totalTotal = 0;
        int lastOrderID;
        
        if (!orderIDs.isEmpty()) {
            lastOrderID = orderIDs.get(orderIDs.size() - 1);
        } else {
            System.out.println("No orders found for this customer.");
            return;
        }
        
        for (Integer orderID : orderIDs) { 

            System.out.println("\tOrder ID: " + orderID);
            System.out.println("\tOrder Date: " + conf.getDataFromID("ORDERS", orderID, "order_date"));

            String query = "SELECT "
                                + "P.ID, P.p_name, OD.prod_price, OD.quantity, OD.line_total "
                            + "FROM "
                                + "ORDERDETAILS OD "
                            + "JOIN "
                                + "PRODUCTS P ON OD.prod_id = P.ID "
                            + "WHERE OD.ID = " + orderID;

            String[] columnHeaders = {"Product ID", "Product Name", "Price", "Quantity", "Line Total"};
            String[] columnNames = {"ID", "p_name", "prod_price", "quantity", "line_total"};

            System.out.println("\nProducts Ordered:");
            conf.viewRecords(query, columnHeaders, columnNames);

            double lineTotal = Double.parseDouble(conf.getDataFromID("ORDERS", orderID, "total_amount"));
            
            String totalAmount = String.format(" %-24.2f|", lineTotal);
            System.out.println("| \t\t\t\t\t\t\t\t\t\tTotal Amount:\t        |" + totalAmount);
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");

            totalTotal += lineTotal;

            if (lastOrderID != orderID){
                System.out.println("\n\n-----------------------------------------------------------------------------------------------------------------------------------\n");
            } else {
                break;
            }
        }

        
        System.out.println("\n\n===================================================================================================================================");
        System.out.println("\t\t\t\t\t       Total Amount Customer Spent: " + totalTotal);
        System.out.println("===================================================================================================================================\n\n");

        
    }
}
