package Interpeter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class Simulator {
	
	double simX,simY,simZ;
	private int port;
	private volatile boolean stop;
	
	public Simulator(int port) {
		this.port=port;
		Random r=new Random();
		simY=r.nextInt(1000);
		simZ=r.nextInt(1000);
		new Thread(()->runServer()).start();
//		new Thread(()->runClient()).start();
	}
	
	private void runClient(){ //Client of the simulator
		while(!stop){
			try {
				Socket interpreter=new Socket("127.0.0.1", port);
				PrintWriter out=new PrintWriter(interpreter.getOutputStream());
				while(!stop){
					System.out.println("Sending simX SimY SimZ");
					out.println(simX+","+simY+","+simZ);
					out.flush();
					try {Thread.sleep(100);} catch (InterruptedException e1) {}
				}
				out.close();
				interpreter.close();
			} catch (IOException e) {
				try {Thread.sleep(1000);} catch (InterruptedException e1) {}
			}
		}
	}
	
	private void runServer(){ //Server of the simulator
		try {
			ServerSocket server=new ServerSocket(port+2);
			server.setSoTimeout(1000);
			while(!stop){
				try{
					Socket client=server.accept();
					System.out.println("My Client has been connected to the simulator server");
					BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
					String line=null;
					while(!(line=in.readLine()).equals("bye")){
						try{
								simX=Double.parseDouble(line.split(" ")[2]);
								System.out.println("The server of the simulator got: "+line+" with value: "+simX);
						}catch(NumberFormatException e){}
					}
					in.close();
					client.close();
				}catch(SocketTimeoutException e){}
			}
			server.close();
		} catch (IOException e) {}
	}

	public void close() {
		stop=true;
	}
}
