import java.io.*;
import java.net.*;
import java.util.Scanner;

class client{
    public static void main(String argv[])throws Exception{
	//for test
	
	//use tcp get real port num
	int req = 13; 				//send 13 to the n_port to require real port num
	String IP = argv[0];
	int negport = Integer.parseInt(argv[1]);
	String sentence = argv[2];
	String realportstr;
	int realport;

	//convert req into string and send it to the n_port

	String require = String.valueOf(req);

	Socket clientSocket = new Socket(IP, negport);  
	DataOutputStream outToServe = new DataOutputStream(clientSocket.getOutputStream());
	
	BufferedReader inFromServe =
	    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
	outToServe.writeBytes(require+"\n");
	
	realportstr = inFromServe.readLine();

	realport = Integer.parseInt(realportstr);
	
	clientSocket.close();

	
	//use udp to do real things
	
	DatagramSocket clientSocket2 = new DatagramSocket();

	InetAddress IPAddress = InetAddress.getByName(IP);

	byte[]sendData = new byte[1024];
	
	byte[]receiveData = new byte[1024];
	
	sendData = sentence.getBytes();

	DatagramPacket sendPacket = 
	    new DatagramPacket(sendData, sendData.length, IPAddress, realport);

	clientSocket2.send(sendPacket);
	
	DatagramPacket receivePacket = 
	    new DatagramPacket(receiveData, receiveData.length);
	
	clientSocket2.receive(receivePacket);
	
	String modified2 = 
	    new String(receivePacket.getData());

	System.out.println("From server "+ modified2);

	clientSocket2.close();
	
    }
}


						 
