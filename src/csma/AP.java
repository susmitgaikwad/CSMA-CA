package csma;

import java.io.*; 
import java.net.*;

public class AP 
{ 

	static Stations st = new Stations(); 
	public AP()
	{
		
	}
	
	public static void sendCTS()
	{
		if(st.isidle())
			System.out.println("CTS recieved");
	}
	
	public static void sendACK(Thread t)
	{
		if(st.isidle())
			System.out.println("ACK received at " + t.getName());
	}
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
