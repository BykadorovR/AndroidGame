package connection;


public class Player {
	private double coordX;
	private double coordY;
	private double fireballX = -1;
	private double fireballY = -1;
	private boolean heroMove;
	private int heroHealth;
	private String ID;
	private boolean exit;
	
	//Конструктор
	public Player(double coordX, double coordY) throws IllegalArgumentException {
		// check invariants
		this.coordX = coordX;
		this.coordY = coordY;
	}

	public String getID() {
		return ID;
	}
	
	public void setID(String _ID) {
		ID= _ID;
	}
	public void setCoordX(double coordX) {
		
		this.coordX = coordX;
	}

	public double getCoordX() {
		return coordX;
	}
	
	public void setCoordY(double coordY) {
		this.coordY = coordY;
	}

	public double getCoordY() {
		return coordY;
	}

	public void setCoordFireballX(double coordX) {
		this.fireballX = coordX;
	}

	public double getCoordFireballX() {
		return fireballX;
	}
	
	public void setCoordFireballY(double coordY) {
		this.fireballY = coordY;
	}

	public double getCoordFireballY() {
		return fireballY;
	}
	
	public boolean getExit() {
		return exit;
	}
	
	public void setExit(boolean exit) {
		this.exit = exit;
	}
	
	public String toString() {
		String res = "Coord X : " + coordX + " Coord Y : " + coordY; 
		return res;
	}

	public boolean getMove() {
		return heroMove;
	}
	public void setMove(boolean move) {
		heroMove = move;
	}
	
	public void setHealth(int health) {
		this.heroHealth = health;
	}
	
	public int getHealth() {
		return heroHealth;
	}
}
