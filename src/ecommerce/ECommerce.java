package ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ECommerce {
    
    public static Connection connectDB() {
        Connection con = null;
        try {
            
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver          
            con = DriverManager.getConnection("jdbc:sqlite:shop.db"); // Establish connection
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Failed: " + e);
        }
        
        return con;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);       
        
        int opt;
        do {    
            try {
                System.out.println("\n\t=== Product Management ===\n");
                System.out.println("1. Products\n2. Orders\n3. View All Customers\n4. Generate Reports\n5. Exit");
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine();
                System.out.println("");

                switch (opt) {
                    case 1:
                        System.out.println("------------------------------------------------------------------");                       
                        ProductsConfig prod = new ProductsConfig();
                        prod.configProducts();   
                        break; 
                    case 2:
                        System.out.println("------------------------------------------------------------------");
                        break;
                    case 3:
                        System.out.println("------------------------------------------------------------------");
                        break;
                    case 4:
                        System.out.println("------------------------------------------------------------------");
                        break;
                    case 5:
                        System.out.println("Exiting...");
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

}
