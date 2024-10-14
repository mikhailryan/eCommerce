package ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Config {
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
    
    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }
        
        try (Connection conn = Config.connectDB();
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            ResultSet rs = pstmt.executeQuery();) {
           
            StringBuilder headerLine = new StringBuilder();
            
            int spacing = 23;
            int lineLength = columnHeaders.length * (spacing + 3) + 1;
            
            for (int i = 0; i < lineLength; i++) {
                 headerLine.append("-");
            }
            headerLine.append("\n| ");

            for (String header : columnHeaders) {
                headerLine.append(String.format("%-" + spacing + "s | ", header));
            }
            
            headerLine.append("\n");
            for (int i = 0; i < lineLength; i++) {
                 headerLine.append("-");
            }

            System.out.println(headerLine.toString());

            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String colName : columnNames) {
                    String value = rs.getString(colName);
                    row.append(String.format("%-" + spacing + "s | ", value != null ? value : "")); 
                }
                System.out.println(row.toString());
            }
            for (int i = 0; i < lineLength; i++) {
                 System.out.print("-");
            }
            System.out.println("");
            
        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());  
        }
    }
    
    public void addRecord(String sql, boolean print, Object... values) {
        try (Connection conn = Config.connectDB(); // Use the connectDB method
             PreparedStatement pstmt = conn.prepareStatement(sql);) {

            // Loop through the values and set them in the prepared statement dynamically
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) values[i]); // If the value is Integer
                } else if (values[i] instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) values[i]); // If the value is Double
                } else if (values[i] instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) values[i]); // If the value is Float
                } else if (values[i] instanceof Long) {
                    pstmt.setLong(i + 1, (Long) values[i]); // If the value is Long
                } else if (values[i] instanceof Boolean) {
                    pstmt.setBoolean(i + 1, (Boolean) values[i]); // If the value is Boolean
                } else if (values[i] instanceof java.util.Date) {
                    pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime())); // If the value is Date
                } else if (values[i] instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) values[i]); // If it's already a SQL Date
                } else if (values[i] instanceof java.sql.Timestamp) {
                    pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]); // If the value is Timestamp
                } else {
                    pstmt.setString(i + 1, values[i].toString()); // Default to String for other types
                }
            }

            pstmt.executeUpdate();
            if(print){
                System.out.println("Record added successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }
    
    public void updateRecord(String updateSql, String selectIdSql, String[] columnNames, Object... values) {
        try (Connection con = connectDB();
            PreparedStatement pst = con.prepareStatement(selectIdSql);
            ResultSet rs = pst.executeQuery();) {

            ResultSetMetaData metaData = rs.getMetaData(); 
            PreparedStatement pstmt = con.prepareStatement(updateSql);

            // Loop through the values and set them in the prepared statement dynamically
            for (int i = 0; i < values.length; i++) {
                
                Object newValue = values[i];

                // If the input is "keep", retrieve the old value from the ResultSet
                if (newValue instanceof String && newValue.equals("keep")) {
                    int columnType = metaData.getColumnType(i + 2); 
                    switch (columnType) {
                        case java.sql.Types.INTEGER:
                            newValue = rs.getInt(columnNames[i]);
                            break;
                        case java.sql.Types.REAL:
                            newValue = rs.getDouble(columnNames[i]);
                            break;
                        default:
                            newValue = rs.getString(columnNames[i]);
                            break;
                    }
                }
                    
                if (newValue instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) newValue);
                } else if (newValue instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) newValue);
                } else if (newValue instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) newValue);
                } else if (newValue instanceof Long) {
                    pstmt.setLong(i + 1, (Long) newValue);
                } else if (newValue instanceof Boolean) {
                    pstmt.setBoolean(i + 1, (Boolean) newValue);
                } else if (newValue instanceof java.util.Date) {
                    pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) newValue).getTime()));
                } else if (newValue instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) newValue);
                } else if (newValue instanceof java.sql.Timestamp) {
                    pstmt.setTimestamp(i + 1, (java.sql.Timestamp) newValue);
                } else {
                    pstmt.setString(i + 1, newValue.toString());
                }
            }

            pstmt.executeUpdate();
            System.out.println("Record updated successfully!");
            
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }
    
    public void deleteRecord(String table, int id, boolean print){
        String sql = "DELETE FROM " + table + " WHERE ID = ?";
        
        try {
            Connection con = connectDB();
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            int success = pst.executeUpdate();
            
            if (print){
                if(success > 0){
                    System.out.println("\nRecord Successfully Deleted.");
                }else{
                    System.out.println("\nNo Record Found with ID: " + id);
                }
            }
            

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public boolean doesIDExist(String table, int id){
        
        String findID = "SELECT * FROM " + table + " WHERE ID = ?";
        
        try (Connection con = connectDB();
            PreparedStatement pst = con.prepareStatement(findID)){
            
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()){
                    return true;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        return false;
    }
    
    public String getDataFromID(String table, int id, String column){
        String findID = "SELECT " + column + " FROM " + table + " WHERE ID = ?";
        String data = "";
        
        try (Connection con = connectDB();      
            PreparedStatement pst = con.prepareStatement(findID)){
            
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                data = rs.getString(column);
            }
                               
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        return data;
    }
    
    public int getID(String sql){
        int id = 0;
        try (Connection con = connectDB();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()){
            
            id = rs.getInt("ID"); 
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        return id;
    }
}
