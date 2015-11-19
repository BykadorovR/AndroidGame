package model;

import java.util.ArrayList;

import android.util.Log;

public class SingleCommunicationModel implements ICommunicationModel {

	private ArrayList<Player> players = new ArrayList<Player>();
	public  Object shared = new Object();
	private String ID;
	
	Player player;
	private float pUX;
	private float pUY;
	private double pAngle;
	
	@Override
	public void Initialize() {	
		ID = "Single";
		player = new Player(500, 100);
		pUX = (float) player.getCoordX();
		pUY = (float) player.getCoordY();
		player.setID(ID);
		player.setHealth(100);
		player.setExit(false);
		players.add(player);
		
		Thread main = new Thread(new Runnable() {
			public void run() {
				while (!player.getExit()) {			
						move();
					}
				}	
		});
		main.start();
	}
	
//...........................................................................
	
	public void move () {
		pAngle = Math.atan2((pUY - (float) player.getCoordY()), (pUX - (float) player.getCoordX()));
		if (Math.abs((float) player.getCoordX() - pUX) > 3 || Math.abs((float) player.getCoordY()	- pUY) > 3) {
			synchronized (shared) {
				
			
			
			player.setCoordX(player.getCoordX()
					+ 3
					* Math.cos(pAngle));
			player.setCoordY(player.getCoordY()
					+ 3
					* Math.sin(pAngle));
			player.setMove(true);
			try {
				shared.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		} else {
			player.setMove(false);
		}
	}
	
	
	@Override
	// пересылаем данные клика
	public void click(double x, double y) {
			pUX = (float) x;
			pUY = (float) y;
	}
	@Override
	public void fireball(double x, double y) {
		
			player.setCoordFireballX(x);
			player.setCoordFireballX(y);
		
	}
	@Override
	public void exit() {
		player.setExit(true);
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
