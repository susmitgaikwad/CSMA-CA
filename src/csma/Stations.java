package csma;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Stations extends Thread
{
	static AP ap = new AP();
	
	static int sifs, difs, data;
	
	static int r = 1000;
	
	public Stations() 
	{
		// TODO Auto-generated constructor stub
	}
		
	public static boolean isidle()
	{ 
		try 
		{
			Socket soc= new Socket("localhost",137);	
			soc.close(); 
			return true; 
		}
		catch (Exception e) 
		{
			return false;
		}
	} 
	
	public static void assignBackoff(Thread t, HashMap<Thread, Integer> map, int CW)
	{
		Random ran = new Random();
		int x = ran.nextInt(CW);
		System.out.println(t.getName()+ " backoff count: "+x);
		if(!map.containsValue(x))
			map.put(t, x);
		else
		{
			System.out.println("\nCollision detected. Trying new backoff value");
			assignBackoff(t, map,CW);
		}
	}
	
	public static void selectMinBackoff(HashMap<Thread, Integer> map)
	{
		
		while(!map.isEmpty())
		{
			try {
				String name = "";
				int min = Collections.min(map.values());
				Thread t;
				
				for(Map.Entry<Thread, Integer> entry: map.entrySet())
				{
					if(entry.getValue().equals(min))
					{
						
						t = entry.getKey();
						System.out.println("\n"+t.getName() + " has least backoff and can start sending");
						map.remove(t);
						run(t,sifs, difs, data);
					}
						
				}
				
				System.out.println("\nChannel is idle again...");
				
			} catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.getMessage();
			}
		}	
	}
	
	public static void run(Thread t, int sifs, int difs, int data)
	{
		try 
        {
	        Boolean bln = false;
			int duration = (r + (sifs*1000) + r + (sifs*1000) + (data*1000) + (sifs*1000) + r) / 1000;
			for (int k = 1; k <= 15; k++) 
			{
				System.out.println("\n" + t.getName() + " attempt : " + k);

				// is idle channel? 
				System.out.println("Is Channel idle? ");
				while (true) {
					if (isidle()) {
						System.out.println("Channel idle");
						System.out.println(t.getName() + " starting to transmit. Duration of " + duration
								+ " seconds announced by " + t.getName());
						System.out.println("RTS sent");
						// wait for RTS time 
						t.sleep(r);
						System.out.println("SIFS");
						// wait for SIFS time 
						t.sleep(sifs*1000);
						ap.sendCTS();
						// wait for CTS time 
						t.sleep(r);
						System.out.println("SIFS");
						// wait for SIFS time 
						t.sleep(sifs*1000);

						// send frame 
						System.out.println(t.getName() + " Data sent");
						t.sleep(data*1000);
						System.out.println("SIFS");
						t.sleep(sifs*1000);

						// ack check 
						if (isidle()) 
						{
							ap.sendACK(t);
							t.sleep(r);
							bln = true;	
							//boolean flag set to true if acknowledgement received
							
							System.out.println("\nDIFS");
							// wait for DIFS time in between transmissions
							t.sleep(difs*1000);
							
							// for next station transmissions
							break;
							
						} 
						
						else 
						{
							System.out.println("ACK not received");
							break;
						}
					} 
					else 
					{
						System.out.println("Busy, back to channel idle check");
					}
				}
				if (bln == true)
					break;
			} 
				
	        
        } 
		catch (InterruptedException e)
	    { 
	    	System.out.println(e);
	    }
	}

	public static void main(String[] args) throws InterruptedException 
	{
		try 
		{
			boolean idle = isidle();
			while (true) 
			{
				Scanner scan = new Scanner(System.in);
				System.out.println("Enter the SIFS duration in seconds:");
				sifs = scan.nextInt();
				System.out.println("Enter the DIFS duration in seconds:");
				difs = scan.nextInt();
				System.out.println("Enter the data frame length for stations in seconds:");
				data = scan.nextInt();
				
				System.out.println("How many stations do you want?");
				int n = scan.nextInt();
				HashMap<Thread, Integer> map = new HashMap<>();
				int cw = n+2;
				for(int i=1;i<n+1;i++)
				{
					Thread t = new Thread("Station "+i);
					assignBackoff(t, map, cw);
				}
				selectMinBackoff(map);
				
				System.out.println("\nTrasmit again? (y/n)");
				char reply = scan.next().charAt(0);
				if(reply == 'n')
					break;
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

}
