package ecommerce;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Orders {
    public void configOrders(){
        Scanner scan = new Scanner(System.in);
        int opt;

        do {    
            try {
                System.out.println("\n\t=== Orders Menu ===\n");
                System.out.println("1. Place New Order");
                System.out.println("2. Update an Existing Order");
                System.out.println("3. Cancel an Order");
                System.out.println("4. View Order History");
                System.out.println("5. Go back..");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 

                switch (opt) {
                    case 1:
                        
                        break;
                    case 2:  
                        break;
                    case 3:
                        break;
                    case 4:
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
