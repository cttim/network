// This client is supposed to send a sentence to the server, and then print the sentence received by the server. 

import java.io.*;  
import java.net.*;  
import java.util.*;



class sender {
	//initial global variable
	public static long starttime;
	public static int receiveportnum;
	public static int seqbase = 0;
	public static boolean status = true;
	public static int size;
	public static void ack() throws Exception {
		// socket used to receive ack from receiver
		DatagramSocket clientsocket = new DatagramSocket(receiveportnum);
		PrintWriter fwack = new PrintWriter(new File("ack.log"));
		while(true){
			byte[]receivedata = new byte[1024];
			DatagramPacket receivepacket =
					new DatagramPacket(receivedata, receivedata.length);
			clientsocket.receive(receivepacket);
				packet p = packet.parseUDPdata(receivepacket.getData());
			int type = p.getType();
			int seq = p.getSeqNum();
			//write seq of ack packet into ack.log
			if(type == 0){
				String seqstr = Integer.toString(seq);
				fwack.println(seqstr);
			}
			//end of writing file ack.log
			if(type == 0 & seq >= seqbase){                          //baseseq < 32
				seqbase = seq+1;
				starttime = System.currentTimeMillis();
				if(seqbase == size){
					break;
				}
			}
			if(type == 0 & seq < seqbase){                  	//baseseq > 32
				if(seqbase%32 == seq){
					seqbase++;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+1)%32 == seq){
					seqbase= seqbase+2;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+2)%32 == seq){
					seqbase = seqbase+3;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+3)%32 == seq){
					seqbase = seqbase+4;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+4)%32 == seq){
					seqbase = seqbase+5;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+5)%32 == seq){
					seqbase = seqbase+6;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+6)%32 == seq){
					seqbase = seqbase+7;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+7)%32 == seq){
					seqbase = seqbase+8;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+8)%32 == seq){
					seqbase = seqbase+9;
					starttime = System.currentTimeMillis();
				}
				else if((seqbase+9)%32 == seq){
					seqbase = seqbase+10;
					starttime = System.currentTimeMillis();
				}
			}
			if(type == 2){
				break;
			}
			
		}
		fwack.close();
		clientsocket.close();
	}

	public static void main(String args[]) throws Exception {  
	//start
		ArrayList<packet>packetlist = new ArrayList<packet>();
		InetAddress ip = InetAddress.getByName(args[0]);
		int udpportnumem = Integer.parseInt(args[1]);
		receiveportnum = Integer.parseInt(args[2]); 
		int seqcounter = 0;
		int seqmid = 0;
		int max = 500;
		long filelength;
		File f = null;
		String data;
		String filename = args[3];
		f= new File(filename);
		filelength = f.length();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		PrintWriter fwseq = new PrintWriter(new File("seqnum.log"));
		//make all packets ready
		while(true){
			if(filelength <= max){
				char[] cbuf2 = new char[(int)filelength];
				br.read(cbuf2, 0, (int) filelength);
				// do shiit
				data = new String(cbuf2);
				packetlist.add(packet.createPacket(seqcounter, data));
				break;
			}
			else{
				char[] cbuf = new char[max];
				br.read(cbuf, 0, max);
				filelength = filelength - max;
				data = new String(cbuf);
				packetlist.add(packet.createPacket(seqcounter,data));
				seqcounter++;
			}
		}
		size = packetlist.size();
		DatagramSocket clientSocket = new DatagramSocket();
		ack a1 = new ack();
		a1.start();
		starttime = System.currentTimeMillis();
		while(true){
			long curtime = System.currentTimeMillis();
			if(curtime-starttime>500){
				status = false;
			}
			if(status == true){		//normal case
				while(seqmid-seqbase<10 & seqmid<packetlist.size()){
					byte[] senddata = new byte[1024];
					senddata = packetlist.get(seqmid).getUDPdata();
					DatagramPacket sendPacket =
							new DatagramPacket(senddata, senddata.length, ip, udpportnumem);
					clientSocket.send(sendPacket);
					//write seqnum of send packet into seqnum.log
					String seqstr1 = Integer.toString(seqmid%32);
					fwseq.println(seqstr1);
					//end of the writing to seqnum.log
					seqmid++;
				}
			}
			if(status == false){		//timeout case
				starttime = System.currentTimeMillis();
				for(int i = seqbase; i<seqmid; i++){
					byte[] senddata = new byte[1024];
					senddata = packetlist.get(i).getUDPdata();
					DatagramPacket sendPacket =
							new DatagramPacket(senddata, senddata.length, ip, udpportnumem);
					clientSocket.send(sendPacket);
					//write seqnum of send packet into seqnum.log
					String seqstr2 = Integer.toString(i%32);
					fwseq.println(seqstr2);
					//end of the writing to seqnum.log	
				}
				status = true;
			}
				
			if(seqbase == packetlist.size()){
				break;
			}
		}
		byte[] finaldata = new byte[1024];
		packet lastp = packet.createEOT(seqbase);
		finaldata = lastp.getUDPdata();
		DatagramPacket lastsend =
			new DatagramPacket(finaldata, finaldata.length, ip, udpportnumem);
		clientSocket.send(lastsend);
		clientSocket.close();
		fwseq.close();
	}


	//end 
}


class ack extends Thread{  
	public void run(){
			try {
				sender.ack();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}



















