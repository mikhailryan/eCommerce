package ecommerce;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Products { 
    Config conf = new Config();
    
    public void configProducts(){
        Scanner scan = new Scanner(System.in);
        int opt;

        do {    
            try {
                System.out.println("\n\t Product Management\n");
                System.out.println("1. Add a Product");
                System.out.println("2. View All Products");
                System.out.println("3. Delete a Product");
                System.out.println("4. Edit a Product");
                System.out.println("5. Go back..");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 
                
                System.out.println("");
                
                boolean emptyTable = conf.isTableEmpty("PRODUCTS");
                switch (opt) {
                    case 1:
                        addProduct(scan);
                        break;

                    case 2:    
                        if (emptyTable) {
                            System.out.println("Products Table is Empty.");
                            break;
                        }
                        System.out.printf("\n%63s\n\n", "=== PRODUCTS LIST ===");
                        viewProducts("SELECT * FROM PRODUCTS");
                        break;

                    case 3:
                        if (emptyTable) {
                            System.out.println("Products Table is Empty.");
                            break;
                        }
                        deleteProduct(scan);
                        break;

                    case 4:
                        if (emptyTable) {
                            System.out.println("Products Table is Empty.");
                            break;
                        }
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
        
        conf.addRecord(sql, true, name, price, stocks);
    }
    
    public void viewProducts(String query) {     
        String[] productsHeaders = {"Product ID", "Product Name", "Price", "Stocks"};
        String[] productsColumns = {"ID", "p_name", "p_price", "p_stocks"};

        conf.viewRecords(query, productsHeaders, productsColumns);
    }
    
    public void deleteProduct(Scanner scan){
        
        System.out.print("Product ID you want to delete: ");
        int id = scan.nextInt();
        
        conf.deleteRecord("PRODUCTS", id, true);
    }
    
    public void editProduct(Scanner scan) {
        
        int id;
        do {
            System.out.print("Product ID you want to edit: ");
            id = scan.nextInt();
            if (!conf.doesIDExist("PRODUCTS", id)) {
                System.out.println("Product ID Doesn't Exist.\n");
            }
        } while (!conf.doesIDExist("PRODUCTS", id));
        scan.nextLine();  
        
        String updateSql = "UPDATE PRODUCTS SET p_name = ?, p_price = ?, p_stocks = ? WHERE id = ?";
        String selectSql = "SELECT * FROM PRODUCTS WHERE ID = " + id;
        
        System.out.println("\nSelected Product:");
        viewProducts(selectSql);

        System.out.println("\nEnter Product Details (type 'keep' to retain current value):");

        System.out.print("New Product Name: ");
        String name = scan.nextLine();

        System.out.print("New Price: ");
        String tempPrice = scan.nextLine();
        Object price = tempPrice.equalsIgnoreCase("keep") ? null : parseDouble(tempPrice);

        System.out.print("New Stocks: ");
        String tempStocks = scan.nextLine();
        Object stocks = tempStocks.equalsIgnoreCase("keep") ? null : parseInteger(tempStocks);
        
        String[] columnNames = {"p_name", "p_price", "p_stocks"};
        conf.updateRecord(updateSql, selectSql, columnNames, true,
                name, price != null ? price : "keep", stocks != null ? stocks : "keep", id);
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for price. Please enter a valid number.");
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for stocks. Please enter a valid number.");
            return null;
        }
    }

}
