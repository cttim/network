import java.io.*;
import java.net.*;
import java.util.*;
//import 

class server{
    public static void main(String argv[])throws Exception{
	System.out.println("SERVER_PORT = 3000");
	ServerSocket welcomeSocket = new ServerSocket(3000);
	
	while(true){
	    Socket connectionSocket = welcomeSocket.accept();
	    
	    BufferedReader inFromClient = 
		new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

	    DataOutputStream outToClient = 
		new DataOutputStream(connectionSocket.getOutputStream());
	    
	    String clientsentence = inFromClient.readLine();
	    
	    int req = Integer.parseInt(clientsentence);
	    //check if the req is 13
	    if(req != 13){}
	    else{		
	    	 //System.out.println(clientsentence);
	    
	   	 //give an random port num and gernerate into string
	    	Random rand = new Random();
	    
	    	int realport = rand.nextInt(2000)+4000;

	   	 String realportstring = Integer.toString(realport);

	    	System.out.println("this is your new port " + realportstring);
	    
	   	 outToClient.writeBytes(realportstring+"\n");


	   	 //use udp to do real things

	   	 DatagramSocket serverSocket = new DatagramSocket(realport);

	   	 byte[] receiveData = new byte[1024];
	    
	   	 byte[] sendData = new byte[1024];

	   	 DatagramPacket receivePacket = 
			new DatagramPacket(receiveData, receiveData.length);

	   	 serverSocket.receive(receivePacket);
	    
	   	 String sentence = new String(receivePacket.getData());
	    
	   	 InetAddress IPAddress = receivePacket.getAddress();
	    
	   	 int port = receivePacket.getPort();

	   	 String reverse = new StringBuffer(sentence).reverse().toString();

	   	 sendData = reverse.getBytes();

	   	 DatagramPacket sendPacket =
			new DatagramPacket(sendData, sendData.length, IPAddress, port);
	
	   	 serverSocket.send(sendPacket);
		}
	}
    }
}
