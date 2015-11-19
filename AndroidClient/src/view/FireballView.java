package view;

import java.util.ArrayList;
import java.util.Map;

import model.ICommunicationModel;
import model.Player;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;

public class FireballView implements ICastView {
	private Scene scene;
	private Game game;

	public FireballView(Scene scene, Game game) {
		this.scene = scene;
		this.game = game;
	}

	@Override
	public void draw(TiledTextureRegion fireball, TiledSprite fireballHUD,
			ICommunicationModel multiComm) {
		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> fireballs = game.getFireballs();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) {
				if (players.get(i).getCoordFireballX() != -1
						&& !fireballs.containsKey(players.get(i).getID())) {
					AnimatedSprite sprite = new AnimatedSprite(-1200, -1200,
							fireball, game.getVertexBufferObjectManager());

					sprite.animate(new long[] { 100, 100, 100, 100, 100, 100 },
							new int[] { 0, 1, 2, 3, 4, 5 }, -1);
					fireballs.put(players.get(i).getID(), sprite);
					scene.attachChild(sprite);
					if (multiComm.getID().equalsIgnoreCase(
							players.get(i).getID()))
						fireballHUD.setCurrentTileIndex(1);

				}
			}
		}
	}

	@Override
	public void move() {
		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> fireballs = game.getFireballs();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) {
				if (players.get(i).getCoordFireballX() != -1
						&& fireballs.containsKey(players.get(i).getID())) {
					fireballs.get(players.get(i).getID()).setPosition(
							(float) players.get(i).getCoordFireballX()
									- fireballs.get(players.get(i).getID())
											.getWidth() / 2,
							(float) players.get(i).getCoordFireballY()
									- fireballs.get(players.get(i).getID())
											.getHeight() / 2);

				}
			}

		}
	}

	@Override
	public void animate(TiledSprite fireballHUD, ICommunicationModel multiComm) {

		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> fireballs = game.getFireballs();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) {
				if (players.get(i).getCoordFireballX() == -1
						&& fireballs.containsKey(players.get(i).getID())
						|| (fireballs.get(players.get(i).getID()) != null && fireballs
								.get(players.get(i).getID())
								.getCurrentTileIndex() >= 6)) {
					if (fireballs.get(players.get(i).getID())
							.getCurrentTileIndex() <= 5)
						fireballs.get(players.get(i).getID()).animate(
								new long[] { 100, 100, 100, 100 },
								new int[] { 6, 7, 8, 9 }, 1);
					if (multiComm.getID().equalsIgnoreCase(
							players.get(i).getID())) {
						fireballHUD.setCurrentTileIndex(0);

					}

				}
			}
		}
	}

	@Override
	public void detach() {
		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> fireballs = game.getFireballs();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) {
				if (players.get(i).getCoordFireballX() == -1
						&& fireballs.containsKey(players.get(i).getID())
						|| (fireballs.get(players.get(i).getID()) != null && fireballs
								.get(players.get(i).getID())
								.getCurrentTileIndex() >= 6)) {
					if (fireballs.get(players.get(i).getID())
							.getCurrentTileIndex() == 9) {
						scene.detachChild(fireballs.get(players.get(i).getID()));
						fireballs.remove(players.get(i).getID());

					}

				}
			}
		}
	}
}
