package csma;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Stations extends Thread
{
	static Thread t1 = new Thread();
	static Thread t2 = new Thread();
	
	static int sifs, difs, data1, data2;
	
	public Stations() 
	{
		// TODO Auto-generated constructor stub
	}
	
	
	public void backoff(Thread t11, Thread t22) throws InterruptedException
	{
		Random ran = new Random();
		int x = ran.nextInt(6) + 0;
		int y = ran.nextInt(6) + 0;
		System.out.println("\nT1 backoff: "+x);
		System.out.println("T2 backoff: "+y);
		
		if(x!=y) 
		{
			int c = Math.min(x, y); // select station with minimum backoff
			if (c == x)
				run(t11, t22, sifs, difs, data1, data2);
			else
				run(t22, t11,  sifs, difs, data2, data1);
		}
		else if(x==y) // collision detection
		{
			System.out.println("Collision Detected! Try again");
			backoff(t1, t2);
		}
		
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
	
	public static void run(Thread t_1,Thread t_2, int sifs, int difs, int data, int data2)
	{
		try 
        {
	        Boolean bln = false;
			int duration = (1000 + (sifs*1000) + 1000 + (sifs*1000) + (data*1000) + (sifs*1000) + 1000) / 1000;
			for (int k = 1; k <= 15; k++) 
			{
				System.out.println("\n" + t_1.getName() + " attempt : " + k);

				// is idle channel? 
				System.out.println("Is Channel idle? ");
				while (true) {
					if (isidle()) {
						System.out.println("Channel idle");
						System.out.println(t_1.getName() + " starting to transmit. Duration of " + duration
								+ " seconds announced by " + t_1.getName());
						System.out.println("RTS sent");
						// wait for RTS time 
						t_1.sleep(1000);
						System.out.println("SIFS");
						// wait for SIFS time 
						t_1.sleep(sifs*1000);
						System.out.println("CTS recieved");
						// wait for CTS time 
						t_1.sleep(1000);
						System.out.println("SIFS");
						// wait for SIFS time 
						t_1.sleep(sifs*1000);

						// send frame 
						System.out.println(t_1.getName() + " Data sent");
						t_1.sleep(data*1000);
						System.out.println("SIFS");
						t_1.sleep(sifs*1000);

						// ack check 
						if (isidle()) {
							System.out.println("ACK received at " + t_1.getName());
							bln = true;
							System.out.println("\nDIFS");
							// wait for DIFS time in between transmissions
							t_1.sleep(difs*1000);
							if(t_2 != null)
								run(t_2, null, sifs, difs, data2, data); // for second station transmission
							else
								System.out.println("Channel is idle again...");
							break;
						} else {
							System.out.println("ACK not received");
							break;
						}
					} else {
						System.out.println("Busy, back to channel idle check");
					}
				}
				if (bln == true) {
					break;
				}
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
			while (idle) 
			{
				Scanner scan = new Scanner(System.in);
				System.out.println("Enter the SIFS duration in seconds:");
				sifs = scan.nextInt();
				System.out.println("Enter the DIFS duration in seconds:");
				difs = scan.nextInt();
				System.out.println("Enter the data frame length for Station 1 in seconds:");
				data1 = scan.nextInt();
				System.out.println("Enter the data frame length for Station 2 in seconds:");
				data2 = scan.nextInt();
				Stations st = new Stations();
				t1.setName("Station 1");
				t2.setName("Station 2");
				st.backoff(t1, t2);
				System.out.println("\nTrasmit again? (y/n)");
				char reply = scan.next().charAt(0);
				if(reply == 'n')
				{
					idle = false;
					break;
				}	
				
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
