package view;

import org.andengine.entity.scene.Scene;


public class FactoryView {
	public IPlayerView createWarView(Scene scene, Game game) {	
		return new WarView(scene, game);
	}
	
	public ICastView createCastView(Scene scene, Game game) {	
		return new FireballView(scene, game);
	}
	
}
