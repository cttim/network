// This client is supposed to send a sentence to the server, and then print the sentence received by the server. 

import java.io.*;  
import java.net.*;  
import java.util.*;


class UDPClient {  
	public int counter;
	public static void main(String args[]) throws Exception {  
	//start
		int seqbase = 0;
		int max = 500;
		int offset = 0;
		int filelength;
		char[] cbuf = new char[500];
		File f = null;
		String data;
		Semaphore sema = new Semaphore(10,true);
		String filename = args[3];
		f= new File(filename);
		filelength = f.length();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		while(true){
			sema.acquire();
			if(filelength < max){
				br.read(cbuf, offset, filelength);
				// do shiit
				data = new String(cbuf);
			}
			else{
				br.read(cbuf, offset, max);
				filelength = filelength - max;
				offset = offset + max;
				data = new String(cbuf);
			}
			packetready = packet.createPacket(1,data);
			byte[] senddata = new byte[1024];
			senddata = packetready.getUDPdata();
			
			
			
			
		/*
		int counter=0;
		ArrayList<packet>packetlist = new ArrayList<packet>();
		String filename = args[0];
		BufferedReader br = new BufferedReader(new FileReader(filename));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while(line != null){
	    	//do shit, convert string line into packet and add into packetlist
	    	packetlist.add(packet.createPacket(counter, line));
		counter++;
		System.out.print(line);
		System.out.print("\n");
		line = br.readLine();
		}*/
	}


	//end
/*
      BufferedReader inFromUser =  
        new BufferedReader(new InputStreamReader(System.in));  
   
      DatagramSocket clientSocket = new DatagramSocket();  
   
      InetAddress IPAddress = InetAddress.getByName("localhost");  //Name of the machine hosting the server
   
      byte[] sendData = new byte[1024];  
      byte[] receiveData = new byte[1024];  
      
      System.out.print("Sentence to Send from the client: ");  
      
      String sentence = inFromUser.readLine();  
      sendData = sentence.getBytes();          
      DatagramPacket sendPacket =  
         new DatagramPacket(sendData, sendData.length, IPAddress, 9876);  
   
      clientSocket.send(sendPacket);  
   
      DatagramPacket receivePacket =  
         new DatagramPacket(receiveData, receiveData.length);  
   
      clientSocket.receive(receivePacket);  
   
      String modifiedSentence =  
          new String(receivePacket.getData());  
   
      System.out.println("FROM SERVER:" + modifiedSentence);  
      clientSocket.close();  
      }*/  
  }
