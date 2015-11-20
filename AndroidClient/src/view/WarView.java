package view;

import java.util.ArrayList;
import java.util.Map;

import model.ICommunicationModel;
import model.Player;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import android.view.Display;

public class WarView implements IPlayerView {

	private Scene scene;
	private Game game;

	public WarView(Scene scene, Game game) {
		this.game = game;
		this.scene = scene;
	}

	@Override
	public void draw(TiledTextureRegion knight, TextureRegion hpTexture,
			ICommunicationModel multiComm) {
		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> fireballs = game.getFireballs();
		Map<String, AnimatedSprite> sprites = game.getSprites();
		Map<String, Sprite> health = game.getHealths();
		Map<String, Integer> forNumberPositionAnimate = game
				.getPositionAnimation();
		Display display = game.getDisplay();
		HUD hud = game.getHUD();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) { // Всегда 4 (но некоторые null)
				if (!sprites.containsKey(players.get(i).getID())) {
					AnimatedSprite sprite = new AnimatedSprite(0, 0, knight,
							game.getVertexBufferObjectManager());
					sprite.setPosition((float) players.get(i).getCoordX()
							- sprite.getWidth() / 2, (float) players.get(i)
							.getCoordY() - sprite.getHeight() * 0.8f);
					Sprite hp = new Sprite(sprite.getX(), sprite.getY()
							- sprite.getHeight() * 0.1f, players.get(i)
							.getHealth() * 2, 15, hpTexture,
							game.getVertexBufferObjectManager());
					if (multiComm.getID().equalsIgnoreCase(
							players.get(i).getID())) {
						hp.setPosition(display.getHeight() / 2, 0);
						hud.attachChild(hp);
					} else {
						hp.setSize(players.get(i).getHealth(), 10);
						scene.attachChild(hp);
					}

					sprite.stopAnimation(1);
					forNumberPositionAnimate.put(players.get(i).getID(), 0);
					sprites.put(players.get(i).getID(), sprite);
					health.put(players.get(i).getID(), hp);

					scene.attachChild(sprite);

				}
			}
		}
	}

	@Override
	public void move(ICommunicationModel multiComm) {
		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> sprites = game.getSprites();
		BoundCamera mBoundChaseCamera = game.getCamera();
		Map<String, Sprite> health = game.getHealths();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) { // Всегда 4 (но некоторые null)
				if (sprites.containsKey(players.get(i).getID())) {
					AnimatedSprite sprite = sprites.get(players.get(i).getID());
					float spriteX = (float) (players.get(i).getCoordX() - sprite
							.getWidth() / 2);
					float spriteY = (float) (players.get(i).getCoordY() - sprite
							.getHeight() * 0.8f);
					synchronized (multiComm.getSync()) {
						sprite.setPosition(spriteX, spriteY);
						multiComm.getSync().notify();
					}
					if (multiComm.getID().equalsIgnoreCase(
							players.get(i).getID())) {
						mBoundChaseCamera.setCenter((float) players.get(i)
								.getCoordX(), (float) players.get(i)
								.getCoordY());
					}
					Sprite hp = health.get(players.get(i).getID());

					if (!multiComm.getID().equalsIgnoreCase(
							players.get(i).getID())) {
						hp.setPosition(sprite.getX(),
								sprite.getY() - sprite.getHeight() * 0.1f);
						hp.setSize(players.get(i).getHealth(), 10);
					} else {
						hp.setSize(players.get(i).getHealth() * 2, 15);
					}
				}
			}
		}

	}

	@Override
	public void animate() {
		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> sprites = game.getSprites();
		Map<String, Integer> forNumberPositionAnimate = game
				.getPositionAnimation();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) { // Всегда 4 (но некоторые null)

				if (sprites.containsKey(players.get(i).getID())) {
					AnimatedSprite sprite = sprites.get(players.get(i).getID());
					float spriteX = (float) (players.get(i).getCoordX() - sprite
							.getWidth() / 2);
					float spriteY = (float) (players.get(i).getCoordY() - sprite
							.getHeight() * 0.8f);
					if (players.get(i).getMove() == false) {
						forNumberPositionAnimate.put(players.get(i).getID(), 0);
						sprite.stopAnimation();
					} else if (players.get(i).getMove() == true) {
						animateSprite(spriteX, sprite.getX(), spriteY,
								sprite.getY(), sprite, players.get(i).getID());
					}
				}
			}
		}
	}

	@Override
	public int detach(ICommunicationModel multiComm) {
		ArrayList<Player> players = game.getPlayers();
		Map<String, AnimatedSprite> fireballs = game.getFireballs();
		Map<String, AnimatedSprite> sprites = game.getSprites();
		Map<String, Sprite> health = game.getHealths();
		Map<String, Integer> forNumberPositionAnimate = game
				.getPositionAnimation();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) { // Всегда 4 (но некоторые null)
			// ===============================================================
				if (players.get(i).getExit() == true) {
					synchronized (multiComm.getSync()) {
						scene.detachChild(sprites.get(players.get(i).getID()));
						scene.detachChild(fireballs.get(players.get(i).getID()));
						scene.detachChild(health.get(players.get(i).getID()));
						fireballs.remove(players.get(i).getID());
						health.remove(players.get(i).getID());
						sprites.remove(players.get(i).getID());
						forNumberPositionAnimate.remove(players.get(i).getID());
						multiComm.getSync().notify();
						return 1;
					}
				}
			}
		}
		return 0;
	}

	public void animateSprite(float sX, float eX, float sY, float eY,
			AnimatedSprite sprite, String ID) {
		Map<String, Integer> forNumberPositionAnimate = game
				.getPositionAnimation();
		float angle = (float) Math.atan2((eY - sY), (eX - sX));
		int number = forNumberPositionAnimate.get(ID);
		if (angle > Math.PI / 6 && angle < Math.PI * 2 / 6) {
			if (number != 2) {

				forNumberPositionAnimate.put(ID, 2);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 21,
						22, 23 }, -1);
			}
		} else if (angle > Math.PI * 2 / 6 && angle < Math.PI * 4 / 6) {
			if (number != 3) {
				forNumberPositionAnimate.put(ID, 3);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 6, 7,
						8 }, -1);
			}
		} else if (angle > Math.PI * 4 / 6 && angle < Math.PI * 5 / 6) {
			if (number != 4) {
				forNumberPositionAnimate.put(ID, 4);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 15,
						16, 17 }, -1);
			}
		} else if (angle > Math.PI * 5 / 6 || angle < -Math.PI * 5 / 6) {
			if (number != 5) {
				forNumberPositionAnimate.put(ID, 5);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 0, 1,
						2 }, -1);
			}
		} else if (angle > -Math.PI * 5 / 6 && angle < -Math.PI * 4 / 6) {
			if (number != 6) {
				forNumberPositionAnimate.put(ID, 6);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 12,
						13, 14 }, -1);
			}
		} else if (angle < -Math.PI * 2 / 6 && angle > -Math.PI * 4 / 6) {
			if (number != 7) {
				forNumberPositionAnimate.put(ID, 7);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 9, 10,
						11 }, -1);
			}
		} else if (angle < -Math.PI / 6 && angle > -Math.PI * 2 / 6) {
			if (number != 8) {
				forNumberPositionAnimate.put(ID, 8);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 18,
						19, 20 }, -1);
			}
		} else if (angle > -Math.PI / 6 && angle < Math.PI / 6 && angle != 0) {
			if (number != 1) {
				forNumberPositionAnimate.put(ID, 1);
				sprite.animate(new long[] { 200, 200, 200 }, new int[] { 3, 4,
						5 }, -1);
			}
		}

	}

}
