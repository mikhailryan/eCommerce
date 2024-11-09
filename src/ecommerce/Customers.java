package ecommerce;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Customers {
    Config conf = new Config();
    Scanner scan = new Scanner(System.in);
    
    public void configCustomers(){
        int opt;

        do {    
            try {
                System.out.println("\n\t Customers \n");
                System.out.println("1. View All Customers");
                System.out.println("2. Add a Customer");
                System.out.println("3. Delete a Customer");
                System.out.println("4. Edit a Customer");
                System.out.println("5. Go back..");
                
                System.out.print("\nEnter Option: ");
                opt = scan.nextInt();
                scan.nextLine(); 
                
                System.out.println("");

                boolean emptyTable = conf.isTableEmpty("CUSTOMERS");
                switch (opt) {
                    case 1:  
                        if (emptyTable) {
                            System.out.println("Customers Table is Empty.");
                            break;
                        }
                        System.out.printf("\n%61s\n", "> CUSTOMERS LIST <");
                        viewCustomers("SELECT * FROM CUSTOMERS");
                        break;

                    case 2:
                        addCustomer();
                        break;

                    case 3:
                        if (emptyTable) {
                            System.out.println("Customers Table is Empty.");
                            break;
                        }
                        deleteCustomer();
                        break;

                    case 4:
                        if (emptyTable) {
                            System.out.println("Customers Table is Empty.");
                            break;
                        }
                        editCustomer();
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
    
    public void addCustomer(){
        System.out.println("Enter Customer Details:\n");
        
        System.out.print("Name: ");
        String name = scan.nextLine();
        System.out.print("Email: ");
        String email = scan.nextLine();
        System.out.print("Address: ");
        String addr = scan.nextLine();
        
        String sql = "INSERT INTO CUSTOMERS (name, email, address) VALUES (?, ?, ?)";          
        conf.addRecord(sql, true, name, email, addr);
    }

    public void viewCustomers(String query) {
        String[] headers = {"Customer ID", "Customer Name", "Email", "Address"};
        String[] columns = {"ID", "name", "email", "address"}; 

        conf.viewRecords(query, headers, columns);
    }

    private void deleteCustomer() {
        System.out.print("Enter ID you want to delete: ");
        int id = scan.nextInt();
        
        conf.deleteRecord("CUSTOMERS", id, true);
    }

    private void editCustomer() {
        int id;        
        boolean idExists;
        do{
            System.out.print("Customer ID you want to delete: ");
            id = scan.nextInt();
            
            idExists = conf.doesIDExist("CUSTOMERS", id);
            if(!idExists){
                System.out.println("Customer ID Doesn't Exist.\n");
            }
        }while(!idExists);
        scan.nextLine();
        
        System.out.println("\nSelected Customer:");
        viewCustomers("SELECT * FROM CUSTOMERS WHERE ID = " + id);
        
        System.out.println("\nEnter Customer Details:");
        
        System.out.print("New Name: ");
        String name = scan.nextLine();
        System.out.print("New Email: ");
        String email = scan.nextLine();
        System.out.print("New Address: ");
        String addr = scan.nextLine();
        
        String[] columnNames = {"name", "email", "address"};
        String updateSql = "UPDATE CUSTOMERS SET name = ?, email = ?, address = ? WHERE id = ?";
        String selectIdSql = "SELECT * FROM CUSTOMERS WHERE ID = " + id;
        conf.updateRecord(updateSql, selectIdSql, columnNames, true, name, email, addr, id);
    }
}
