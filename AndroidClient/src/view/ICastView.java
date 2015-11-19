package view;

import model.ICommunicationModel;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;

public interface ICastView {
	public void draw(TiledTextureRegion fireball, TiledSprite fireballHUD, ICommunicationModel multiComm);
	public void move();
	public void animate(TiledSprite fireballHUD, ICommunicationModel multiComm);
	public void detach();
	
}
