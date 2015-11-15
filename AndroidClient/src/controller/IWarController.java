package controller;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.TiledSprite;

import client.Client;

public interface IWarController {
	
	public void controlCastFireball(float xFromTouch, float yFromTouch, BoundCamera mBoundChaseCamera, TiledSprite fireballHUD, Client client);
	
	public void controlMove();
	
	public void controlExit();
}
