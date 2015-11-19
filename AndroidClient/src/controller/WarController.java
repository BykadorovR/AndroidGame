package controller;

import model.ICommunicationModel;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.TiledSprite;

import view.Game;

public class WarController implements IWarController {

	private int _move = 0;
	private int _fireball = 0;

	private int castFireball = 0;

	private float xFromTouch, yFromTouch;
	private ICommunicationModel client;
	private Game game;

	@Override
	public void controlHandle(float xFromTouch, float yFromTouch) {
		this.xFromTouch = xFromTouch;
		this.yFromTouch = yFromTouch;
		castFireball();
		controlEnd();
	}

	public WarController(ICommunicationModel client, Game game) {
		this.client = client;
		this.game = game;
	}

	@Override
	public void controlMove(boolean status) {
		if (status)
			_move = 1;
		else _move = 0;
	}

	@Override
	public void controlCastFireball(boolean status) {
		if (status) {
			castFireball = 0;
			_fireball = 1;
		} else {
			castFireball = 0;
			_fireball = 0;
		}
		
	}

	public void castFireball() {
		TiledSprite fireballHUD = game.getFireball();
		BoundCamera mBoundChaseCamera = game.getCamera();
		if ( _fireball == 1 && xFromTouch < fireballHUD.getWidth()
				+ mBoundChaseCamera.getCenterX() - mBoundChaseCamera.getWidth()
				/ 2
				&& yFromTouch < fireballHUD.getHeight()
						+ mBoundChaseCamera.getCenterY()
						- mBoundChaseCamera.getHeight() / 2
				&& fireballHUD.getCurrentTileIndex() == 0) {
			castFireball = 1;
		} else if (_fireball == 1 && xFromTouch < fireballHUD.getWidth()
				+ mBoundChaseCamera.getCenterX() - mBoundChaseCamera.getWidth()
				/ 2
				&& yFromTouch < fireballHUD.getHeight()
						+ mBoundChaseCamera.getCenterY()
						- mBoundChaseCamera.getHeight() / 2
				&& fireballHUD.getCurrentTileIndex() == 1) {
			castFireball = -1;
		}
	}

	public void controlEnd() {

		if (castFireball == 1) {
			castFireball = 2;
		} else if (castFireball == 2) {
			client.fireball(xFromTouch, yFromTouch);
			castFireball = 0;
		} else if (castFireball == -1) {
			castFireball = 0;
		} else if (_move == 1 && castFireball == 0) {
			client.click(xFromTouch, yFromTouch);
		}

	}

}
