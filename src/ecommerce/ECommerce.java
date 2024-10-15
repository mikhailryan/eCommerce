package ecommerce;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ECommerce {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);    
        Config conf = new Config();
        
        Products prod = new Products();
        Orders ord = new Orders();
        Customers cus = new Customers();
        
        int opt;
        do {    
            try {
                System.out.println("\n\t=== eCommerce Management System ===\n");
                System.out.println("1. Products");
                System.out.println("2. Orders");
                System.out.println("3. Customers");
                System.out.println("4. Generate Receipts");
                System.out.println("5. Exit");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine();
                System.out.println("");
                
                switch (opt) {
                    case 1:
                        System.out.println("------------------------------------------------------------------");
                        prod.configProducts();   
                        break; 
                    case 2:
                        System.out.println("------------------------------------------------------------------");
                        ord.configOrders();
                        break;
                    case 3:
                        System.out.println("------------------------------------------------------------------");
                        cus.configCustomers();
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
