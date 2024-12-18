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
        Reports rep = new Reports();
        
        int opt;
        do {    
            try {
                System.out.println("\n eCommerce Management System");
                System.out.println("1. Orders");
                System.out.println("2. Products");
                System.out.println("3. Customers");
                System.out.println("4. Generate Reports");
                System.out.println("5. Exit");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine();
                System.out.println("");

                System.out.println("--------------------------------------------------------------------------------------------------");
                
                switch (opt) {
                    case 1:
                        ord.configOrders();
                        break; 
                    case 2:
                        prod.configProducts();  
                        break;
                    case 3:
                        cus.configCustomers();
                        break;
                    case 4:
                        rep.generateReports();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        System.out.println("--------------------------------------------------------------------------------------------------");
                        break;
                    case 69:
                        System.out.println("hehe nice");
                    default:
                        System.out.println("Invalid Option.");
                        System.out.println("--------------------------------------------------------------------------------------------------");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                System.out.println("--------------------------------------------------------------------------------------------------");

                scan.nextLine(); 
                opt = -1; 
            }
        } while (opt != 5);        

    }

}
