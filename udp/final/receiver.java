// This server is supposed to receive a sentence from the client, capitalizes it and sends it back to the client.

import java.io.*;  
import java.net.*;  
   
class receiver {  
  public static void main(String args[]) throws Exception { 
    int expectseq = 0;
    InetAddress ip = InetAddress.getByName(args[0]);
    int udpportemu = Integer.parseInt(args[1]);     //send ack port
    int udpportrec = Integer.parseInt(args[2]);     //receive packet port
    String outputfile = args[3];
    DatagramSocket serverSocket = new DatagramSocket(udpportrec); 
    byte[] receiveData = new byte[1024];  
    byte[] sendData  = new byte[1024]; 
    byte[] writeData = new byte[1024];
    PrintWriter fwarrival = new PrintWriter(new File("arrival.log"));
    PrintWriter fwout = new PrintWriter(new File(outputfile));
    while(true){
    		DatagramPacket receivepacket =
    				new DatagramPacket(receiveData, receiveData.length);
    		serverSocket.receive(receivepacket);
    		packet p = packet.parseUDPdata(receivepacket.getData());
		//write seqnum of packet into arrival.log
		if(p.getType() == 1){
			String seqstr = Integer.toString(p.getSeqNum());
			fwarrival.println(seqstr);
		}
		//end of the writing file
    		if(p.getType() == 1){
    			int seqnum = p.getSeqNum();
    			if(seqnum == expectseq){
    				//record the right data and write into file
    				writeData = p.getData();
    				String sentence = new String(writeData);
    				//write data into file 
				fwout.print(sentence);
				//end of writing data into file
    				//send new ack
    				packet ackpacket = packet.createACK(expectseq);
    				sendData = ackpacket.getUDPdata();
    				DatagramPacket sendpacket =
    						new DatagramPacket(sendData, sendData.length, ip, udpportemu);
    				serverSocket.send(sendpacket);
    				expectseq++;
				expectseq = expectseq%32;
    			}
    			else{
    				//send old ack
				if(expectseq == 0){}
				else{
    					packet ackpacket = packet.createACK(expectseq-1);
    					sendData = ackpacket.getUDPdata();
    					DatagramPacket sendpacket =
    							new DatagramPacket(sendData, sendData.length, ip, udpportemu);
    					serverSocket.send(sendpacket);
				}		
    			}
    		}
    		if(p.getType() == 2){
			//packet transmission done this is finish packet
    			packet ackpacket = packet.createEOT(expectseq);
				sendData = ackpacket.getUDPdata();
				DatagramPacket sendpacket =
						new DatagramPacket(sendData, sendData.length, ip, udpportemu);
				serverSocket.send(sendpacket);
				break;
    		}
    }
	fwarrival.close();
	fwout.close();
	serverSocket.close();    	
  }
}
