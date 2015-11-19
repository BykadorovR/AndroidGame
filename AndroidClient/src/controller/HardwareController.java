package controller;

import view.Game;
import model.ICommunicationModel;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class HardwareController implements IHardwareController{
	private ICommunicationModel client;
	private Game game;
	private int _exit = 0;
	
	public HardwareController(ICommunicationModel client, Game game) {
		this.client = client;
		this.game = game;
	}
	
	
	
	public void exit() {
		AlertDialog.Builder quitDialog = new AlertDialog.Builder(game);
		quitDialog.setTitle("Выход: Вы уверены?");

		quitDialog.setPositiveButton("Таки да!", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				client.exit();
				game.finish();
			}

		});

		quitDialog.setNegativeButton("Нет", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});

		quitDialog.show();
		
	}



	@Override
	public void controlExit(boolean status) {
		if (status)
			_exit = 1;
		else _exit = 0; 
	}



	@Override
	public void controlHandle() {
		if (_exit == 1) {
			exit();
		}
		
	}

}
