package controller;

import view.Game;
import model.ICommunicationModel;

public class FactoryController {
	
	public IWarController createWarController(ICommunicationModel client, Game game) {	
		return new WarController(client, game);
	}
	
	public IHardwareController createHardwareController(ICommunicationModel client, Game game) {
		return new HardwareController(client, game);
	}
}
