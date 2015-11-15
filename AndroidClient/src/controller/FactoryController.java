package controller;

public class FactoryController {
	
	public IWarController createWarController() {	
		return new WarController();
	}
	
}
