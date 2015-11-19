package model;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MultiplayerCommunicationModel implements ICommunicationModel {

	private ArrayList<Player> players = new ArrayList<Player>();
	private final Object coord = new Object();
	public static final Object shared = new Object();
	private double positionX;
	private double positionY;
	private boolean _startPosition = true;
	
	private double fireballX = -1;
	private double fireballY = -1;

	private String position;
	private String ID;
	private Socket sock = null;
	private Thread main;
	
	private IFramerModel lengthFramer;
	private ICoderModel binCoder;
	
	@Override
	public void Initialize() {

		for (int i = 0; i < 4; i++) {
			players.add(i, null);
		}
//...........................................................................
//Initialize server connection and create framer and coder
		main = new Thread(new Runnable() {
			public void run() {
				String destAddr = "192.168.1.3"; // Destination address
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

				FactoryModel fModel = new FactoryModel();
				try {
					lengthFramer = fModel.createLengthFramerModel(sock.getInputStream());
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				binCoder = fModel.createBinCoderModel();
				
//...........................................................................
//
				Thread fromServer = new Thread(new Runnable() {
					public void run() {
						try {
							// Receive vote response
							ArrayList<byte[]> req;

							while ((req = lengthFramer.getCountOfMessages()) != null) {

								for (int i = 0; i < req.size(); i++) {
									synchronized (shared) {
										Player msgTMP = binCoder.fromWire(req
												.get(i));
										players.set(i, msgTMP);
										if (msgTMP.getExit() == true) {
											shared.wait();
										}
										
										//Наш игрок
										
										if (getID().equalsIgnoreCase(players.get(i).getID()) && _startPosition) {
											positionX = players.get(i).getCoordX();
											positionY = players.get(i).getCoordY();
											_startPosition = false;
										}
										
										if (msgTMP.getID().equalsIgnoreCase(ID)
												&& msgTMP.getExit() == true) {
											System.out.println("OOOOPS");
											return;
										}
									}
								}
								// Иначе будем обрабатывать вышедших игроков (у
								// них вообще true в exit, так что повиснут все)
								for (int i = req.size(); i < 4 - req.size(); i++) {
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

//...........................................................................

				System.out.println("Connected to server...");
				OutputStream out = null;
				try {
					out = sock.getOutputStream();
				} catch (IOException e1) {
					e1.printStackTrace();
				} // To server

				// Инициализируем сообщение
				Player toMsg = new Player(-1000, -1000);
				toMsg.setID(ID);
				toMsg.setHealth(100);
				toMsg.setExit(false);
				// Отправляем сообщение
				while (true) {
					byte[] encodedMsg = null;
					try {
						encodedMsg = binCoder.toWire(toMsg);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Sending Inquiry (" + encodedMsg.length
							+ " bytes): ");
					System.out.println(toMsg);
					try {
						lengthFramer.frameMsg(encodedMsg, out);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// Выходим, если ввели exit

					// Ждем когда произойдет смена координат
					synchronized (coord) {
						try {
							coord.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (position != null && position.equalsIgnoreCase("exit")) {
						position = null;
						// Специальный флаг у объекта
						toMsg.setExit(true);
						try {
							encodedMsg = binCoder.toWire(toMsg);
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println("Sending Inquiry ("
								+ encodedMsg.length + " bytes):  EXITEXITEXIT");
						System.out.println(toMsg);
						try {
							lengthFramer.frameMsg(encodedMsg, out);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					// Инициализируем объект новыми координатами
					toMsg.setCoordFireballX(fireballX);
					toMsg.setCoordFireballY(fireballY);
					fireballX = -1;
					fireballY = -1;
					toMsg.setCoordX(positionX);
					toMsg.setCoordY(positionY);
				}
				// Если попали сюда, значит мы вышли и должны дождаться
				// Пока поток, который принимает объекты с сервера,
				// Получит сообщение о завершение со стороны сервера и
				// завершится сам
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
	
//...........................................................................
	
	@Override
	// пересылаем данные клика
	public void click(double x, double y) {
		synchronized (coord) {
			positionX = x;
			positionY = y;
			coord.notify();
		}
	}
	@Override
	public void fireball(double x, double y) {
		synchronized (coord) {
			fireballX = x;
			fireballY = y;
			coord.notify();
		}
	}
	@Override
	public void exit() {
		synchronized (coord) {
			position = "exit";
			coord.notify();
		}
	}
	@Override
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	@Override
	public String getID() {
		return ID;
	}

	@Override
	public Object getSync() {
		// TODO Auto-generated method stub
		return shared;
	}
}
