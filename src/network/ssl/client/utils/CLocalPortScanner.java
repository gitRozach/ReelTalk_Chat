package network.ssl.client.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CLocalPortScanner 
{
	private int fromPort;
	private int toPort;
	private int maxResults;
	private volatile boolean running;
	private CPortMode portMode;
	private ArrayList<Integer> availablePorts;
	private ArrayList<Integer> unavailablePorts;
	
	public CLocalPortScanner(CPortMode mode)
	{
		this.fromPort = 0;
		this.toPort = 110000;
		this.maxResults = 10000;
		this.running = false;
		this.portMode = mode;
		this.availablePorts = new ArrayList<Integer>();
		this.unavailablePorts = new ArrayList<Integer>();
	}
	
	public static enum CPortMode
	{
		TCP, UDP;
	}
	
	public static void main(String[] args)
	{
		CLocalPortScanner finder = new CLocalPortScanner(CPortMode.TCP);
		finder.startScanning();
		finder.printAvailable();
		finder.printUnavailable();
	}
	
	public void startScanning()
	{
		if(!this.running)
		{
			if(this.portMode == CPortMode.TCP)
				this.scanTCP();
			else
				this.scanUDP();
		}
		else
		{
			System.err.println("(CAvailablePortFinder) Find process is still running!");
		}
	}
	
	private void scanTCP()
	{
		availablePorts.clear();
		unavailablePorts.clear();
		this.running = true;
		
		System.out.println("(CAvailablePortFinder) Scanning TCP Ports...");
		boolean[] freeEnginePortFound = {false};
		
		Thread cyc = new Thread(() -> 
		{
			ServerSocket engine = null;
			int enginePort = 11_000;
			
			while(!freeEnginePortFound[0])
			{
				try(ServerSocket newSocket = new ServerSocket(enginePort))
				{
					engine = newSocket;
					freeEnginePortFound[0] = true;
					break;
				}
				catch(IOException io)
				{
					++enginePort;
				}
			}
			
			System.out.println("(CAvailablePortFinder) Scan Server Port: " + enginePort);
			
			while(this.running)
			{
				try 
				{
					engine.accept();
				} 
				catch (IOException e) {}
			}
		});
		cyc.start();
		
		while(!freeEnginePortFound[0])
		{
			try
			{
				Thread.sleep(500L);
			}
			catch(InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
		
		for(int currentPort = this.fromPort; currentPort <= this.toPort && this.availablePorts.size() < this.maxResults; ++currentPort)
		{
			try(Socket runner = new Socket())
			{
				runner.bind(new InetSocketAddress("localhost", currentPort));
				this.availablePorts.add(currentPort);
			}
			catch(IOException io2)
			{
				this.unavailablePorts.add(currentPort);
				//io2.printStackTrace();
			}
			if(!this.running)
				break;
		}
		
		System.out.println("(CAvailablePortFinder) Find process finished.");
		
		this.stop();
	}
	
	private void scanUDP()
	{
		System.out.println("(CAvailablePortFinder) Scanning UPD Ports...");
		
		availablePorts.clear();
		unavailablePorts.clear();
		this.running = true;
		
		for(int currentPort = this.fromPort; currentPort <= this.toPort && this.availablePorts.size() < this.maxResults; ++currentPort)
		{
			try(DatagramSocket runner = new DatagramSocket(currentPort))
			{
				this.availablePorts.add(currentPort);
			}
			catch(IOException io2)
			{
				this.unavailablePorts.add(currentPort);
			}
			
			if(!running)
				break;
		}
		
		System.out.println("(CAvailablePortFinder) Find process finished.");
		
		this.stop();
	}
	
	private void stop()
	{
		this.running = false;
	}
	
	public void printAvailable()
	{
		if(availablePorts == null)
			System.out.println("[ ]");
		
		System.out.print("[ ");
		for(int i = 0; i < this.availablePorts.size()-1; ++i)
			System.out.print(this.availablePorts.get(i) + ", ");
		System.out.println((this.availablePorts.size() > 1 ? this.availablePorts.get(this.availablePorts.size()-1) : "") + "] ");
	}
	
	public void printUnavailable()
	{
		if(unavailablePorts == null)
			System.out.println("[ ]");
		
		System.out.print("[ ");
		for(int i = 0; i < this.unavailablePorts.size()-1; ++i)
			System.out.print(this.unavailablePorts.get(i) + ", ");
		System.out.println((this.unavailablePorts.size() > 1 ? this.unavailablePorts.get(this.unavailablePorts.size()-1) : "") + "] ");
	}
	
	/*
	 * 
	 */
	
	public int getStartPort() {return this.fromPort;}
	public void setStartPort(int value) {this.fromPort = value;}
	public int getFinishPort() {return this.toPort;}
	public void setFinishPort(int value) {this.toPort = value;}
	public int getMaxResults() {return this.maxResults;}
	public void setMaxResults(int value) {this.maxResults = value;}
	
	public boolean isChecking() {return this.running;}
	public CPortMode getPortMode() {return this.portMode;}
	
	public ArrayList<Integer> getAvailablePorts(){return this.availablePorts;}
	public ArrayList<Integer> getUnavailablePorts(){return this.unavailablePorts;}
}
