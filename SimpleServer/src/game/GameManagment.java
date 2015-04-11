package game;

import java.io.IOException;
import java.net.Socket;

import connection.Framer;
import connection.LengthFramer;
import connection.Player;
import connection.PlayerCoder;

public class GameManagment {
	private final Object object = new Object();
	private Socket clntSock;
	private PlayerCoder coder;
	private Player msg = new Player(500, 100);
	private Player msgUpdate = null;
	private int speed = 3;
	private int lengthFire = 0;
	
	private int speedFireball = 5;
	private double endFireballX = -1;
	private double endFireballY = -1;
	private double angle;
	private double angleFireball;

	public GameManagment(PlayerCoder coder, final Socket _clntSock) {
		this.clntSock = _clntSock;
		this.coder = coder;

	}

	public void runPlayer() {

		Thread client = new Thread(new Runnable() {
			

			public void run() {
				try {
					Framer framer = new LengthFramer(clntSock.getInputStream());
					byte[] req;
					while ((req = framer.nextMsg()) != null) {
						msgUpdate = coder.fromWire(req);
						if (msgUpdate.getCoordX() == -1000 && msgUpdate.getCoordY()==-1000) {
							msgUpdate.setCoordX(msg.getCoordX());
							msgUpdate.setCoordY(msg.getCoordY());
						} //TO DO: DO NORMAL METHOD FOR FIRST INITIALIZATION POSITION OF PLAYERS
						endFireballX = msgUpdate.getCoordFireballX();
						endFireballY = msgUpdate.getCoordFireballY();
						
						
						synchronized (object) {
							//Remove setter for exit flag (in if clause and below it)
						msg.setExit(msgUpdate.getExit());
						if (msg.getExit() == true) {
							System.out.println("Bye-bye");
							return;
						}
						msg.setHealth(msgUpdate.getHealth());
						msg.setID(msgUpdate.getID());
						
						}	
						double uX;
						double cX;
						double uY;
						double cY;
						if (endFireballX == -1) {
						uX = msgUpdate.getCoordX();
						cX = msg.getCoordX();
						uY = msgUpdate.getCoordY();
						cY = msg.getCoordY();
						angle = Math.atan2((uY - cY), (uX - cX));
						} else 
						if (endFireballX != -1 && lengthFire == 0) {
							System.out.println("POPALI!&");
							lengthFire = 60;
							msg.setCoordFireballX(msg.getCoordX());
							msg.setCoordFireballY(msg.getCoordY());
							uX = endFireballX;
							cX = msg.getCoordX();
							uY = endFireballY;
							cY = msg.getCoordY();
							System.out.println( uX + " ux " + cX + " cx " + uY + " uy " + cY + "cy ");
							angleFireball =  Math.atan2((uY - cY), (uX - cX));
			
						}

					}
				} catch (IOException ioe) {
					System.err.println("game managment error: "
							+ ioe.getMessage());
				}
			}

		});
		client.start();
	}
	public Object getObject() {
		return object;
	}
	public Player getMsg() {
		return msg;
	}

	public Socket getSocket() {
		return clntSock;
	}

	public double getEndX() {
		return msgUpdate.getCoordX();
	}

	public double getEndY() {
		return msgUpdate.getCoordY();
	}

	public double getAngle() {
		return angle;
	}
	public double getFireAngle() {
		return angleFireball;
	}
	public int getSpeed() {
		return speed;
	}
	public int getFireSpeed() {
		return speedFireball;
	}
	public int getLengthFire() {
		return lengthFire;
	}
	public void setLengthFire(int length) {
		this.lengthFire = length;
	}
}
