//router program

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.ArrayList;

class router {
	public static int id;
	public static InetAddress nseip;
	public static int nseport;
	public static int routerport;
	//create a 2D array to store all link state
	public static int[][] linkstate = new int [5][5];
	//create a 2D array for linkcost
	public static int[] linkcost= new int[20];
	//create a vector of distance
	public static int[] tempdistance = new int[5];
	public static int[] rib = new int[5];
	public static ArrayList<Integer> finishq = new ArrayList<Integer>();
	public static int[] p = new int[5];
	
    public static int findp(int router){
	//int hp = -1;
	if(p[router-1] == -1){
		return -1;
	}
	if(p[router-1] == 0){
		return 0;
	}
	while(p[router-1] != id){
		router = p[router-1];
	}
	return router;
    }
	public static int minimum(){
		int haha = 999999;
		for(int i=0; i<5; i++){
			if(find(i+1)){}
			else{
				if(tempdistance[i]<haha){
					haha = tempdistance[i];
				}
			}
		}
		return haha;
	}
	
	public static int countnbr(int num){
		int counter=0;
		for(int i=0; i<5; i++){
			if(linkstate[num-1][i] != 0){
				counter++;
			}
		}
		return counter;
	}
	public static boolean find(int router){
		for(int i=0; i<finishq.size(); i++){
			if(finishq.get(i)==router){
				return true;
			}
		}
		return false;
	}
	
	public static int findminimum(){
		int haha = 999999;
		int routerid = -100;
		for(int i=0; i<5; i++){
			if(find(1+i)){}
			else{
				if(tempdistance[i]<haha){
					haha = tempdistance[i];
					routerid = i;
				}
			}
		}
		if(routerid<0){
			return -1;
		}
		return routerid;
	}
	
	public static void updatetable(){
		int routerid = 1 + findminimum();
		for(int i=0; i<5; i++){
			if(linkstate[routerid-1][i] != 0){
				int routeridnext=0;
				for(int j=0; j<5; j++){
					for(int k=0; k<5; k++){
						if(j != routerid-1 & linkstate[j][k] == linkstate[routerid-1][i]){
							routeridnext = j+1;
						}
					}
				}
				if(routeridnext != 0){
					if(tempdistance[routeridnext-1] >= tempdistance[routerid-1] + linkcost[linkstate[routerid-1][i]-1]){
						tempdistance[routeridnext-1] = tempdistance[routerid-1] + linkcost[linkstate[routerid-1][i]-1];
					
					p[routeridnext-1] = routerid;
					}
				}
			}
		}
		finishq.add(routerid);
	}
	
	public static void findrib(){
		for(int i=0; i<5; i++){
			tempdistance[i] = 999999;
		}
		for(int j=0; j<5; j++){
			p[j] = -1;
		}
		finishq.clear();
		p[id-1] = 0;
		tempdistance[id-1] = 0;
		
		while(finishq.size()< 5 & minimum() != 999999){
			
			updatetable();
		}
	}
	public static void main(String args[]) throws Exception {
		//start to initial the variable
		id = Integer.parseInt(args[0]);
		nseip = InetAddress.getByName(args[1]);
		nseport = Integer.parseInt(args[2]);
		routerport = Integer.parseInt(args[3]);

		//create the socket
		DatagramSocket routersocket = new DatagramSocket(routerport);
		
		//initial the file writer
		PrintWriter writer = new PrintWriter(new File("router"+id+".log"));
		
		//prepare the initial packet
		ByteBuffer initbuffer = ByteBuffer.allocate(4);
		initbuffer.order(ByteOrder.LITTLE_ENDIAN);
		initbuffer.putInt(id);
		DatagramPacket initpacket =
				new DatagramPacket(initbuffer.array(), initbuffer.array().length, nseip, nseport);
		//send initial package to nse
		routersocket.send(initpacket);

		//record the packet into log file
		writer.println("R" + id + " send init_pkt to nse: sender " + id);
		writer.flush();
		
		//receive the db_circuit
		byte[] receivedbc = new byte[1024];
		DatagramPacket receivepacket = 
			    new DatagramPacket(receivedbc, receivedbc.length);
		routersocket.receive(receivepacket);
		
		//System.out.println(receivepacket.getLength());
		ByteBuffer dbc = 
				ByteBuffer.wrap(receivedbc).order(ByteOrder.LITTLE_ENDIAN);

		//send hello packet and record the dbc into linkstate and linkcost array
		int counter = dbc.getInt();										//nbr num
		for(int i=0; i<counter; i++){
			int linkid = dbc.getInt();
			int cost = dbc.getInt();
			//record the dbc packet
			writer.println("R" + id + " receive db_circuit: linkid " + linkid + ", linkcost "+cost);
			writer.flush();
			//finish recording
			for(int j=0; i<5; j++){
				if(linkstate[id-1][j] == 0){
					linkstate[id-1][j] = linkid;
					linkcost[linkid-1] = cost;
					break;
				}	
			}
			ByteBuffer hellosend = ByteBuffer.allocate(8);
			hellosend.order(ByteOrder.LITTLE_ENDIAN);
			hellosend.putInt(id);
			hellosend.putInt(linkid);
			DatagramPacket hellopacket =
				new DatagramPacket(hellosend.array(), hellosend.array().length, nseip, nseport);

			//record sending hello packet
			writer.println("R" + id + "send hello_pkt: routerid "+ id +", link_id: "+linkid);
			writer.flush();
			//finish recording

			routersocket.send(hellopacket);	
		}
		//finish sending hello package 
		
		//start to receive hello and lsp packet and do real things
		while(true){
			byte[] receivedatareal = new byte[1024];
			DatagramPacket receivepac =
				new DatagramPacket(receivedatareal, receivedatareal.length);
			routersocket.receive(receivepac);
			//check packet type
			int length = receivepac.getLength();
			if(length == 8){
			//its hello packet
				ByteBuffer hellorec = 
						ByteBuffer.wrap(receivedatareal).order(ByteOrder.LITTLE_ENDIAN);
				int routerid = hellorec.getInt();
				int via = hellorec.getInt(); 
			//record receiving hello packet
				writer.println("R"+id+" receive hello_pkt: routerid "+routerid+", link_id "+via);
				writer.flush();
			//finish recording

			//send all lspdu packet
				int ls_inkid;
				int ls_inkcost;
				int ls_sender = id;
				int ls_routerid;
				for(int i=0; i<5; i++){
					for(int j=0; j<5; j++){
						if(linkstate[i][j] != 0){
							ls_routerid = i+1;
							ls_inkid = linkstate[i][j];
							ls_inkcost = linkcost[linkstate[i][j] - 1];
							ByteBuffer lsph = ByteBuffer.allocate(20);
							lsph.order(ByteOrder.LITTLE_ENDIAN);
							lsph.putInt(ls_sender);
							lsph.putInt(ls_routerid);
							lsph.putInt(ls_inkid);
							lsph.putInt(ls_inkcost);
							lsph.putInt(via);
							DatagramPacket lsphpacket =
								new DatagramPacket(lsph.array(), lsph.array().length, nseip, nseport);
							//record sending lspdu
							writer.println("R"+ id+" send LS PDU: sender" + ls_sender + ", router_id "+ ls_routerid + ", link_id "+ls_inkid +", cost "+ls_inkcost+ ", via "+via);
							writer.flush();
							//finish recording

							routersocket.send(lsphpacket);
						}
					}
				}
								
			}
			
			if(length == 20){
				//this lsppdu
				ByteBuffer lspdu =
						ByteBuffer.wrap(receivedatareal).order(ByteOrder.LITTLE_ENDIAN);
				int lspdu_sender = lspdu.getInt();
				int lspdu_router_id = lspdu.getInt();
				int lspdu_link_id = lspdu.getInt();
				int lspdu_cost = lspdu.getInt();
				int lspdu_via = lspdu.getInt();
				boolean sendstate = true;
				//record receiving lspdu
				writer.println("R"+ id+" receive LS PDU: sender" + lspdu_sender + ", router_id "+ lspdu_router_id + ", link_id "+lspdu_link_id +", cost "+lspdu_cost+ ", via "+lspdu_via);
				writer.flush();
				//finish recording

				//add to linkstate routerid has linkid with cost cost
				for(int i=0; i<5; i++){
					if(linkstate[lspdu_router_id-1][i] == lspdu_link_id){
						sendstate = false;
						break;
					}
					if(linkstate[lspdu_router_id-1][i] == 0){
						//case need to update linkstate
						linkstate[lspdu_router_id-1][i] = lspdu_link_id;
						linkcost[lspdu_link_id - 1] = lspdu_cost;

						router.findrib();
						//record new linkstate
						writer.println("topology start------------------");
						for(int l=0; l<5; l++){
							writer.println("R"+id+"->R"+(l+1)+" nbr link "+ countnbr(l+1));
							for(int j=0; j<5; j++){
								if(linkstate[l][j] != 0){
									writer.println("R"+ id +"->R"+ (l+1) +" link "+ linkstate[l][j] + " cost "+linkcost[linkstate[l][j]-1]);
								}
							}
						}
						writer.println("topology ends------------------");
						writer.flush();
						//record new rib
						writer.println("#RIB start--------------------");
						for(int h=0; h<5; h++){
							writer.print("R"+id+"->R"+(h+1)+"->");
							if(findp(h+1) == -1){
								writer.print("INIF");
							}
							else if(findp(h+1) == 0){
								writer.print("Local");
							}
							else{
								writer.print("R"+findp(h+1));
							}
							writer.print(",");
							if(tempdistance[h] == 999999){
								writer.println("INIF");
							}
							else{
								writer.println(tempdistance[h]);
							}
						}
						writer.println("#RIB end-----------------------");
						writer.flush();
						//finish recording
						break;
					}
				}			
					
				//finish update linkstate and rib

				//send update state to other nbr				
				for(int j=0; j<5; j++){
					if(sendstate == false){break;}
					if(linkstate[id-1][j] == lspdu_via){}
					if(linkstate[id-1][j] == 0){}
					else{
						lspdu_sender = id;
						int newvia = linkstate[id-1][j];
						ByteBuffer updatelspdu = ByteBuffer.allocate(20);
						updatelspdu.order(ByteOrder.LITTLE_ENDIAN);
						updatelspdu.putInt(lspdu_sender);
						updatelspdu.putInt(lspdu_router_id);
						updatelspdu.putInt(lspdu_link_id);
						updatelspdu.putInt(lspdu_cost);
						updatelspdu.putInt(newvia);
						DatagramPacket updatelsppacket =
								new DatagramPacket(updatelspdu.array(), updatelspdu.array().length, nseip, nseport);
						//record sending lspdu
						writer.println("R"+ id+" send LS PDU: sender" + lspdu_sender + ", router_id "+ lspdu_router_id + ", link_id "+lspdu_link_id +", cost "+lspdu_cost+ ", via "+newvia);
						writer.flush();
						//finish recording
						routersocket.send(updatelsppacket);
					}
				}
				
			}
			
		}
		
	}
}



		
