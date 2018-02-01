package socs.network.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RouterThread extends Thread {

	Socket socket;
	RouterDescription rd;

	// assuming that all routers are with 4 ports
	Link[] ports;

	RouterThread() {
	}

	RouterThread(Socket socket, RouterDescription rd, Link[] ports) throws IOException {
		this.socket = socket;
		this.rd = rd;
		this.ports = ports;
	}

	public void run() {
		try {
			DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());			
			
			// as server, accept request from clients
			String message = null;
			while ((message = inFromClient.readUTF()) != null) {
				//hello
				if (message.startsWith("Hello ")) {
					
					System.out.println(message);
					
					/*
					String[] arguments = message.split(" ");
					String simulatedIP = arguments[2];
					
					for(for (int i = 0; i < 4; i++) {){
						if(link.router2.getSimulatedIPAddress().equals(simulatedIP)){
						}
					} */

					if (ports[0].router2.getStatus() == null) {
						ports[0].router2.setStatus(RouterStatus.INIT);
						outToClient.writeUTF("Hello From " + rd.getSimulatedIPAddress() + "\nSet " + rd.getSimulatedIPAddress() + " state to TWO_WAY");
					} else {
						ports[0].router2.setStatus(RouterStatus.TWO_WAY);
						outToClient.writeUTF("Done");
						
					}
				//r2 router description
				}else{
					String[] arguments = message.split(" ");

					String processIP = arguments[0];
					Short processPort = Short.parseShort(arguments[1]);
					String simulatedIP = arguments[2];

					// add port in server
					String hasSpot = "false";
					for (int i = 0; i < 4; i++) {
						if (ports[i] == null) {
							hasSpot = "true";
							RouterDescription r2 = new RouterDescription(processIP, processPort, simulatedIP);
							ports[i] = new Link(rd, r2);
							break;
						}
					}
					outToClient.writeUTF(hasSpot);					
				}
			}
			socket.sendUrgentData(0xff);
			outToClient.flush();
			
			socket.close();

		} catch (IOException e) {
			//System.out.println("Unable to read from standard in");
			//System.exit(1);
			Thread.currentThread().interrupt();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    return;
		}
	}
}
