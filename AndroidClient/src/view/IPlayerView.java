package view;

import model.ICommunicationModel;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

public interface IPlayerView {
	public void draw(TiledTextureRegion knight, TextureRegion hp, ICommunicationModel multiComm);
	public void move(ICommunicationModel multiComm);
	public void animate();
	public void detach(ICommunicationModel multiComm);
	
}
