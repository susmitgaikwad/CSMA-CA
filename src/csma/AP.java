package csma;

import java.io.*; 
import java.net.*;

public class AP 
{ 
      public static void main(String[] args) 
      { 
          try 
         { 
             System.out.println("Access Point");
             while(true) 
             { 
                ServerSocket ss = new ServerSocket(137);
                System.out.println("Waiting for transmission");
                ss.accept(); 
                ss.close(); 
                System.out.println("Connected");
             } 
         }
         catch(Exception e) 
        { 
               System.out.println(e); 
        } 
    } 
}