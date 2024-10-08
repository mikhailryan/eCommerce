package ecommerce;

import static ecommerce.Config.connectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Products { 
    Config conf = new Config();
    
    public void configProducts(){
        Scanner scan = new Scanner(System.in);
        int opt;

        do {    
            try {
                System.out.println("\n\t=== Product Management ===\n");
                System.out.println("1. View All Products");
                System.out.println("2. Add a Product");
                System.out.println("3. Delete a Product");
                System.out.println("4. Edit a Product");
                System.out.println("5. Go back..");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 

                switch (opt) {
                    case 1:  
                        System.out.println("\n\t\t\t\t   === PRODUCTS LIST ===\n");
                        viewProducts("SELECT * FROM PRODUCTS");
                        break;
                    case 2:              
                        System.out.println("\n\t\t=== ADDING NEW PRODUCT ===\n");
                        addProduct(scan);
                        break;
                    case 3:
                        System.out.println("\n\t\t=== DELETING A PRODUCT ===\n");
                        deleteProduct(scan);
                        break;
                    case 4:
                        System.out.println("\n\t\t=== EDIT A PRODUCT ===\n");
                        editProduct(scan);
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
    
    public void addProduct(Scanner scan){
        
        System.out.println("Enter Product Details:");
        
        System.out.print("Product Name: ");
        String name = scan.nextLine();
        
        System.out.print("Product Price: ");
        double price = scan.nextDouble();
        
        System.out.print("Product Stock: ");
        int stocks = scan.nextInt();
                
        String sql = "INSERT INTO PRODUCTS (p_name, p_price, p_stocks) VALUES (?, ?, ?)";       
        
        conf.addRecord(sql, name, price, stocks);
    }
    
    public void viewProducts(String query) {     
        String[] productsHeaders = {"Product ID", "Product Name", "Price", "Stocks"};
        String[] productsColumns = {"ID", "p_name", "p_price", "p_stocks"};

        conf.viewRecords(query, productsHeaders, productsColumns);
    }
    
    public void deleteProduct(Scanner scan){
        
        System.out.print("Product ID you want to delete: ");
        int id = scan.nextInt();
        
        conf.deleteRecord("products", id);
    }
    
    public void editProduct(Scanner scan){
        
        System.out.print("Enter ID you want to edit: ");
        int id = scan.nextInt();      

        String[] columnHeaders = {"Product Name", "Price", "Stocks"};
        String[] columnNames = {"p_name", "p_price", "p_stocks"};

        conf.updateRecord("PRODUCTS", columnHeaders, columnNames, id);       
    }  
}
