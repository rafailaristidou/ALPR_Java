/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author rafail
 */
public class MySqlconnect {
    Connection conn=null;


    public static Connection dbConnection()    {
        
        try{
        Class.forName ("com.mysql.jdbc.Driver");
        Connection conn=DriverManager.getConnection ("jdbc:mysql://localhost/alpr?user=root&password=");
        
        return conn;
        }
    catch(Exception e){
    System.out.print("Fail conn");
    return null;
}
    }
}

