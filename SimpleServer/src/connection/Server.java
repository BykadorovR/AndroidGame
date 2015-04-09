package connection;

import game.GameManagment;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class Server {

	static ArrayList<GameManagment> list = new ArrayList<GameManagment>();
	static ArrayList<Framer> framer = new ArrayList<Framer>();
	static ArrayList<byte[]> players = new ArrayList<byte[]>();

	public static void main(String[] args) throws IOException {
		int servPort = 80;
		final PlayerCoder coder = new PlayerBinCoder();
		// Create a server socket to accept client connection requests
		ServerSocket servSock = new ServerSocket(servPort);
		new Thread() {
			public void run() {
				while (true) {
					try {
						sleep(20);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					int number = -1;
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getMsg().getID() != null) {
							// Формируем сначала список объектов (уже
							// переведенных в байты)
							// Берем сообщение и кодируем его
							synchronized (list.get(i).getObject()) {
								
														try {
								if (Math.abs(list.get(i).getMsg().getCoordX()
										- list.get(i).getEndX()) > list.get(i)
										.getSpeed()
										|| Math.abs(list.get(i).getMsg()
												.getCoordY()
												- list.get(i).getEndY()) > list
												.get(i).getSpeed()) {
									Player player = list.get(i).getMsg();
									player.setCoordX(player.getCoordX()
											+ list.get(i).getSpeed()
											* Math.cos(list.get(i).getAngle()));
									player.setCoordY(player.getCoordY()
											+ list.get(i).getSpeed()
											* Math.sin(list.get(i).getAngle()));
									player.setMove(true);
								} else {
									Player player = list.get(i).getMsg();
									player.setCoordX(list.get(i).getEndX());
									player.setCoordY(list.get(i).getEndY());
									player.setMove(false);
								}
								
								if (list.get(i).getLengthFire()!=0) {
									list.get(i).setLengthFire(list.get(i).getLengthFire()-1);
									Player player = list.get(i).getMsg();
									player.setCoordFireballX(player.getCoordFireballX()
											+ list.get(i).getFireSpeed()
											* Math.cos(list.get(i).getFireAngle()));
									player.setCoordFireballY(player.getCoordFireballY()
											+ list.get(i).getFireSpeed()
											* Math.sin(list.get(i).getFireAngle()));
								} else {
									Player player = list.get(i).getMsg();
									player.setCoordFireballX(-1);
									player.setCoordFireballY(-1);
								}
						
								
									if (list.get(i).getMsg().getExit() == true) {
										number = i;
									}
										players.add(coder.toWire(list.get(i)
											.getMsg()));
							
							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.out.println("TUM");
								e.printStackTrace();

							}
							}
						}
					}
					 
					// Передаем всем список байтов
					for (int i = 0; i < list.size(); i++) {
						
//						System.out.println(list.get(i).getMsg().getExit()
//								+ " EXIT" + i + " i");
						
						if (list.get(i).getMsg().getID() != null) {
							try {
								framer.get(i).frameMsgList(
										players,
										list.get(i).getSocket()
												.getOutputStream());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.out.println("TAM");
								e.printStackTrace();
								return;
							}
						}
					}
					if (number != -1) {
						System.out.println("second loop" +number+" i");
						list.remove(number);
						number = -1;
					}
					players.clear();
				}
			}
		}.start();

		while (true) { // Run forever, accepting and servicing connections
			final Socket clntSock = servSock.accept(); // Get client connection
			framer.add(new LengthFramer(clntSock.getInputStream()));
			SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
			System.out.println("Handling client at " + clientAddress);
			GameManagment gm = new GameManagment(coder, clntSock);
			gm.runPlayer();
			list.add(gm);

		}
	}

}
