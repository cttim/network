// This server is supposed to receive a sentence from the client, capitalizes it and sends it back to the client.

import java.io.*;  
import java.net.*;  
   
class UDPServer {  
  public static void main(String args[]) throws Exception { 
    System.out.println(555);
    int expectseq = 0;
    InetAddress ip = InetAddress.getByName(args[0]);
    int udpportemu = Integer.parseInt(args[1]);     //send ack port
    int udpportrec = Integer.parseInt(args[2]);     //receive packet port
    String outputfile = args[3];
    DatagramSocket serverSocket = new DatagramSocket(udpportrec); 
    System.out.println(888);
    //DatagramSocket serverSocket2 = new DatagramSocket(udpportemu); //use to send ack
    byte[] receiveData = new byte[1024];  
    byte[] sendData  = new byte[1024]; 
    byte[] writeData = new byte[1024];
    while(true){
    		DatagramPacket receivepacket =
    				new DatagramPacket(receiveData, receiveData.length);
    		serverSocket.receive(receivepacket);
		System.out.println("server get packet");
    		packet p = packet.parseUDPdata(receivepacket.getData());
    		if(p.getType() == 1){
    			int seqnum = p.getSeqNum();
    			if(seqnum == expectseq){
    				//record the right data and write into file
    				writeData = p.getData();
    				String sentence = new String(writeData);
    				//should add into file
    				System.out.print(sentence);
    				//send new ack
    				packet ackpacket = packet.createACK(expectseq);
    				sendData = ackpacket.getUDPdata();
    				DatagramPacket sendpacket =
    						new DatagramPacket(sendData, sendData.length, ip, udpportemu);
    				serverSocket2.send(sendpacket);
    				expectseq++;
    			}
    			else{
    				//send old ack
    				packet ackpacket = packet.createACK(expectseq-1);
    				sendData = ackpacket.getUDPdata();
    				DatagramPacket sendpacket =
    						new DatagramPacket(sendData, sendData.length, ip, udpportemu);
    				serverSocket2.send(sendpacket);		
    			}
    		}
    		if(p.getType() == 2){
    			packet ackpacket = packet.createEOT(expectseq);
				sendData = ackpacket.getUDPdata();
				DatagramPacket sendpacket =
						new DatagramPacket(sendData, sendData.length, ip, udpportemu);
				serverSocket.send(sendpacket);
				break;
    		}
    }  
  }
}
