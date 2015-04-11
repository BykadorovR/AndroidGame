package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
	public Client() {

	}
	
	private ArrayList<Player> players = new ArrayList<Player>();
	private final Object coord = new Object();
	public static final Object shared = new Object();
	private Framer framer;
	private double positionX;
	private double positionY;

	private double fireballX = -1;
	private double fireballY = -1;
	
	private String position;
	private String ID;
	private Socket sock = null;
	private Thread main;
	private PlayerCoder coder = new PlayerBinCoder();
	
	public void runThread() {
		
		
		for (int i = 0; i < 4; i++) {
			players.add(i, null);
		}

		main = new Thread(new Runnable() {
			public void run() {
				String destAddr = "192.168.1.6"; // Destination address
				int destPort = 80; // Destination ports
				try {
					sock = new Socket(destAddr, destPort);
				} catch (UnknownHostException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				ID = sock.getLocalAddress().toString()
						.concat(String.valueOf(sock.getLocalPort()));
				
				try {
					framer = new LengthFramer(sock.getInputStream());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				Thread fromServer = new Thread(new Runnable() {
					public void run() {
						try {
							// Receive vote response
							ArrayList<byte[]> req;

							while ((req = framer.getCountOfMessages()) != null) {
								
								for (int i = 0; i < req.size(); i++) {
									synchronized (shared) {
									Player msgTMP = coder.fromWire(req.get(i));
									players.set(i, msgTMP);
									if (msgTMP.getExit() == true) {
											shared.wait();
									}
									
									
									if (msgTMP.getID().equalsIgnoreCase(ID)
											&& msgTMP.getExit() == true) {
										System.out.println("OOOOPS");
										return;
									}
									}
								}
								//Иначе будем обрабатывать вышедших игроков (у них вообще true в exit, так что повиснут все)
								for (int i=req.size(); i<4-req.size(); i++) {
									players.set(i, null);
								}

								
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("TUT");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				fromServer.start();

				System.out.println("Connected to server...");
				OutputStream out = null;
				try {
					out = sock.getOutputStream();
				} catch (IOException e1) {
					e1.printStackTrace();
				} // To server

				//Инициализируем сообщение
				Player toMsg = new Player(-1000, -1000);
				toMsg.setID(ID);
				toMsg.setHealth(100);
				toMsg.setExit(false);
				//Отправляем сообщение
				while (true) {
					byte[] encodedMsg = null;
					try {
						encodedMsg = coder.toWire(toMsg);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Sending Inquiry (" + encodedMsg.length
							+ " bytes): ");
					System.out.println(toMsg);
					try {
						framer.frameMsg(encodedMsg, out);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					//Выходим, если ввели exit
					
					//Ждем когда произойдет смена координат
					synchronized (coord) {
						try {
							coord.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (position != null && position.equalsIgnoreCase("exit")) {
						position = null;
						//Специальный флаг у объекта
						toMsg.setExit(true);
						try {
							encodedMsg = coder.toWire(toMsg);
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println("Sending Inquiry ("
								+ encodedMsg.length + " bytes):  EXITEXITEXIT");
						System.out.println(toMsg);
						try {
							framer.frameMsg(encodedMsg, out);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					//Инициализируем объект новыми координатами
					toMsg.setCoordFireballX(fireballX);
					toMsg.setCoordFireballY(fireballY);
					fireballX = -1;
					fireballY = -1;
					toMsg.setCoordX(positionX);
					toMsg.setCoordY(positionY);
				}
				//Если попали сюда, значит мы вышли и должны дождаться
				//Пока поток, который принимает объекты с сервера,
				//Получит сообщение о завершение со стороны сервера и завершится сам
				try {
					fromServer.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		main.start();
	}
	//пересылаем данные клика
	public void click(double x, double y) throws InterruptedException {
		synchronized (coord) {
			positionX = x;
			positionY = y;
			coord.notify();
		}
	}
	
	public void fireball(double x, double y) throws InterruptedException {
		synchronized (coord) {
			fireballX = x;
			fireballY = y;
			coord.notify();
		}
	}
	public void exit() {
		synchronized (coord) {
			position = "exit";
			coord.notify();
		}
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public String getID() {
		return ID;
	}

}
