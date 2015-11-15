package controller;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.TiledSprite;

import android.util.Log;
import client.Client;

public class WarController implements IWarController {

private int modeOffireball = 0; 
	
@Override
	public void controlCastFireball(float xFromTouch, float yFromTouch, BoundCamera mBoundChaseCamera, TiledSprite fireballHUD, Client client) {
	try {
		if (xFromTouch < fireballHUD.getWidth()+mBoundChaseCamera.getCenterX()-mBoundChaseCamera.getWidth()/2 
				&& yFromTouch < fireballHUD.getHeight()+mBoundChaseCamera.getCenterY()-mBoundChaseCamera.getHeight()/2
				&& fireballHUD.getCurrentTileIndex() == 0 || modeOffireball == 1) {
			if (modeOffireball == 1) {
				modeOffireball = 0;
				client.fireball(xFromTouch, yFromTouch);
			} else {
				modeOffireball ++;
			}
			
		} else {
			client.click(xFromTouch, yFromTouch);
		}
		
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
@Override
	public void controlMove() {
		
	}


@Override
	public void controlExit() {
		
	}
}
