package model;

import java.util.ArrayList;

public interface ICommunicationModel {
	public void fireball(double x, double y);
	public void click(double x, double y);
	public void exit();
	public ArrayList<Player> getPlayers();
	public String getID();
	public void Initialize();
	public Object getSync();
	
}
