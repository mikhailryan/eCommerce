package ecommerce;

import static ecommerce.ECommerce.connectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ProductsConfig {
    String name;
    double price;
    int stocks;
    
    public void viewProducts(){
        String sql = "SELECT * FROM PRODUCTS";
        try {
            Connection con = connectDB();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet result = pst.executeQuery();
            
            if (!result.isBeforeFirst()) {  
                System.out.println("The products table is empty."); return;
            }
            
            System.out.println("\n\t\t      === PRODUCTS LIST ===\n");
            System.out.println("------------------------------------------------------------------");
            System.out.printf("%-15s %-20s %-20s %-10s\n", "Product ID", "Product Name", "Product Price", "Stocks");
            System.out.println("------------------------------------------------------------------");
            while(result.next()){
                int id = result.getInt("ID");
                String pname = result.getString("p_name");
                double pprice = result.getDouble("p_price");
                int pstocks = result.getInt("p_stocks");
                
                System.out.printf("  %-13d   %-18s   %-18s   %-10d\n", id, pname, pprice, pstocks);
            }
            System.out.println("------------------------------------------------------------------");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void addProduct(Scanner scan){
        System.out.println("\n\t\t=== ADDING NEW PRODUCT ===\n");
        System.out.println("Enter Product Details:");
        
        System.out.print("Product Name: ");
        this.name = scan.nextLine();
        
        System.out.print("Product Price: ");
        this.price = scan.nextDouble();
        
        System.out.print("Product Stock: ");
        this.stocks = scan.nextInt();
                
        String sql = "INSERT INTO PRODUCTS (p_name, p_price, p_stocks) VALUES (?, ?, ?)";       
        try {
            Connection con = connectDB();           
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setInt(3, stocks);
            pst.executeUpdate();
            System.out.println("\nProduct Successfully Inserted");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void deleteProduct(Scanner scan){
        System.out.println("\n\t\t=== DELETING A PRODUCT ===\n");
        
        System.out.print("Product ID you want to delete: ");
        int id = scan.nextInt();
        
        String sql = "DELETE FROM PRODUCTS WHERE ID = ?";
        try {
            Connection con = connectDB();
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            int success = pst.executeUpdate();
            
            if(success > 0){
                System.out.println("\nProduct Successfully Deleted.");
            }else{
                System.out.println("\nNo Product Found with ID: " + id);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving data: " + e.getMessage());
        }
    }
    
    public void editProduct(Scanner scan){
        System.out.println("\n\t\t=== EDIT A PRODUCT ===\n");
        
        String findID = "SELECT * FROM PRODUCTS WHERE ID = ?";
        String sql = "UPDATE PRODUCTS SET p_name = ?, p_price = ?, p_stocks = ? WHERE ID = ?";
        
        System.out.print("Product ID you want to Edit: ");
        int id = scan.nextInt();
        scan.nextLine();
        
        try {
            Connection con = connectDB();
            
            PreparedStatement findIDpst = con.prepareStatement(findID);
            findIDpst.setInt(1, id);
            ResultSet rs = findIDpst.executeQuery();
            
            if(!rs.next()){
                System.out.println("Product with ID " + id + " Doesn't Exist.");
                return;
            }
            
            int pid = rs.getInt("ID");
            String pname = rs.getString("p_name");
            double pprice = rs.getDouble("p_price");
            int pstocks = rs.getInt("p_stocks");
            
            System.out.println("\n Selected  Product");
            System.out.printf("%-15s %-20s %-20s %-10s\n", "Product ID", "Product Name", "Product Price", "Stocks");
            System.out.println("------------------------------------------------------------------");
            System.out.printf("  %-13d   %-18s   %-18s   %-10d\n", pid, pname, pprice, pstocks);
            System.out.println("------------------------------------------------------------------");
            
            System.out.print("Enter new Product Name: ");
            String newName = scan.nextLine();
            
            System.out.print("Enter new Product Price: ");
            double newPrice = scan.nextDouble();
            
            System.out.print("Enter new Stocks: ");
            int newStocks = scan.nextInt();
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, newName);
            pst.setDouble(2, newPrice);
            pst.setInt(3, newStocks);
            pst.setInt(4, id);
            pst.executeUpdate();
            
            System.out.println("\nProduct was Edited Successfully!");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }
    
    public void products(Scanner scan){
        int opt;

        do {    
            try {
                System.out.println("\n\t=== Product Management ===\n");
                System.out.println("1. View All Products\n2. Add a Product\n3. Delete a Product\n4. Edit a Product\n5. Go back..");
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 

                switch (opt) {
                    case 1:
                        System.out.println("------------------------------------------------------------------");
                        viewProducts();
                        System.out.println("\n------------------------------------------------------------------");
                        break;
                    case 2:
                        System.out.println("------------------------------------------------------------------");
                        addProduct(scan);
                        System.out.println("\n------------------------------------------------------------------");
                        break;
                    case 3:
                        System.out.println("------------------------------------------------------------------");
                        deleteProduct(scan);
                        System.out.println("\n------------------------------------------------------------------");
                        break;
                    case 4:
                        System.out.println("------------------------------------------------------------------");
                        editProduct(scan);
                        System.out.println("\n------------------------------------------------------------------");
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

}
