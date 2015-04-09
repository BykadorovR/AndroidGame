package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.MotionEvent;

public class Game extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	protected BitmapTextureAtlas mBitmapTextureAtlas;
	
	ArrayList<Player> players;
	Map<String, AnimatedSprite> sprites = new HashMap<String, AnimatedSprite>();
	Map<String, AnimatedSprite> fireballs = new HashMap<String, AnimatedSprite>();
	private TiledTextureRegion fireballIcon;
	private TextureRegion background;
	private TiledSprite fireballHUD;
	private TiledTextureRegion knight;
	private TiledTextureRegion fireball;
	private Sprite backSprite;
	private RepeatingSpriteBackground mGrassBackground;
	protected BoundCamera mBoundChaseCamera;
	Map<String, Integer> forNumberPosition = new HashMap<String, Integer>();
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		mBoundChaseCamera.setBoundsEnabled(true);
		mBoundChaseCamera.setBounds(-200, -200, 1400+200, 1200+200);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mBoundChaseCamera
				);
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 2048, 2048,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA); // Атлас
		fireball = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "fireball.png", 0, 1201 ,10,1);
		fireballIcon = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "fireball_icon.png", 1750, 0 ,2,1);
		knight = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this,"knight.png", 1401, 0, 3, 8);
		background = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "field.png", 0,0);
		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getTextureManager(), 
				AssetBitmapTextureAtlasSource.create(this.getAssets(), "gfx/lava_tile.png"), this.getVertexBufferObjectManager());
		mBitmapTextureAtlas.load();
	}

	Client client;
	Sprite yourSprite;
	Sprite notSprite;
	boolean _clickForAnimation = false;
	int counter= 0;
	@Override
	protected Scene onCreateScene() {
		final Scene scene = new Scene();
		client = new Client();
		client.runThread();
		
		//Check max amount players in room (4) while our "player" is not exist
		//When "player" enter in the room, camera set his center as own center
		boolean _checkOurEnter = false;
		
		while (_checkOurEnter==false) {
		players = client.getPlayers();
			for (int i=0; i<4; i++) {
				if (players.get(i)!=null)
				if (client.getID().equalsIgnoreCase(players.get(i).getID())) {
				mBoundChaseCamera.setCenter((float) players.get(i).getCoordX(),(float) players.get(i).getCoordY());
				_checkOurEnter = true;
				}
			}
		}
		
		
		// КАСАНИЕ
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			private float xFromTouch;
			private float yFromTouch;
			int fireball = 0;
			@Override
			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
				MotionEvent localMotionEvent = pSceneTouchEvent
						.getMotionEvent(); // Для простоты обработки
											// всевозможных касаний
				if (localMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					
					xFromTouch = pSceneTouchEvent.getX();
					yFromTouch = pSceneTouchEvent.getY();
					
					try {
						if (xFromTouch < fireballHUD.getWidth()+mBoundChaseCamera.getCenterX()-mBoundChaseCamera.getWidth()/2 
								&& yFromTouch < fireballHUD.getHeight()+mBoundChaseCamera.getCenterY()-mBoundChaseCamera.getHeight()/2
								&& fireballHUD.getCurrentTileIndex() == 0 || fireball == 1) {
							if (fireball == 1 ) {
								fireball = 0;
								
								client.fireball(xFromTouch, yFromTouch);
							} else 
							fireball ++;
							
						} else {
							client.click(xFromTouch, yFromTouch);
						}
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return false;
			}
		});

		scene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				players = client.getPlayers();
				for (int i = 0; i < players.size(); i++) {
					if (players.get(i) != null) { // Всегда 4 (но некоторые
						
						
						
//						======================================
//								=====================fireball====================
						if (players.get(i).getCoordFireballX() != -1 && !fireballs.containsKey(players.get(i).getID())) {
							AnimatedSprite sprite = new AnimatedSprite(-1200, -1200, fireball,
									getVertexBufferObjectManager());
							
							sprite.animate(new long[] {100, 100, 100, 100, 100, 100}, new int[] {0,1,2,3,4,5}, -1);
							fireballs.put(players.get(i).getID(), sprite);
							scene.attachChild(sprite);
							if (client.getID().equalsIgnoreCase( players.get(i).getID()))
							fireballHUD.setCurrentTileIndex(1);

						} else
						if (players.get(i).getCoordFireballX() != -1 && fireballs.containsKey(players.get(i).getID())) {
							
							
							
							fireballs.get(players.get(i).getID()).setPosition(
									(float) players.get(i).getCoordFireballX()
											- fireballs.get(
													players.get(i).getID())
													.getWidth() / 2,
									(float) players.get(i).getCoordFireballY()
											- fireballs.get(
													players.get(i).getID())
													.getHeight() / 2);
							
						}
						else if (players.get(i).getCoordFireballX() == -1 && fireballs.containsKey(players.get(i).getID()) || 
							(fireballs.get(players.get(i).getID())!= null && fireballs.get(players.get(i).getID()).getCurrentTileIndex()>=6)) {
							if (fireballs.get(players.get(i).getID()).getCurrentTileIndex()<=5)
							fireballs.get(players.get(i).getID()).animate(new long[] {100, 100, 100, 100}, new int[] {6,7,8,9}, 1);
							if (fireballs.get(players.get(i).getID()).getCurrentTileIndex()==9) {
							scene.detachChild(fireballs.get(players.get(i)
									.getID()));
							fireballs.remove(players.get(i).getID());
							
							if (client.getID().equalsIgnoreCase(players.get(i).getID())) {
								fireballHUD.setCurrentTileIndex(0);
								
							}
							}
							
						}
						
//						===============================================================
						if (players.get(i).getExit() == true) {
							synchronized (Client.shared) {
								scene.detachChild(sprites.get(players.get(i)
										.getID()));
								scene.detachChild(fireballs.get(players.get(i).getID()));
								fireballs.remove(players.get(i).getID());
								
								sprites.remove(players.get(i).getID());
								forNumberPosition.remove(players.get(i).getID());
								Client.shared.notify();
								return;
							}
						}
						if (!sprites.containsKey(players.get(i).getID())) {
							AnimatedSprite sprite = new AnimatedSprite(0,0, knight,
									getVertexBufferObjectManager());
							sprite.setPosition((float) players.get(i).getCoordX()- sprite.getWidth()/2, 
									(float) players.get(i).getCoordY()-sprite.getHeight()*0.8f);
							sprite.stopAnimation(1);
							forNumberPosition.put(players.get(i).getID(), 0);
							sprites.put(players.get(i).getID(), sprite);
							scene.attachChild(sprite);
							
						} else if (sprites.containsKey(players.get(i).getID())) {
							
							
							AnimatedSprite sprite = sprites.get(players.get(i).getID());
							float spriteX = (float) (players.get(i).getCoordX() -sprite.getWidth()/2);
							float spriteY = (float) (players.get(i).getCoordY() -sprite.getHeight()*0.8f) ;
							if (players.get(i).getMove() == false) {
								forNumberPosition.put(players.get(i).getID(), 0);
								sprite.stopAnimation();
							} else if (players.get(i).getMove() == true) {
								animateSprite(spriteX, sprite.getX(), spriteY, sprite.getY(), sprite, players.get(i).getID());
							}
							sprite.setPosition(spriteX, spriteY);
							if (client.getID().equalsIgnoreCase(players.get(i).getID())) {
								mBoundChaseCamera.setCenter((float) players.get(i).getCoordX(),(float) players.get(i).getCoordY());
							}
							
						}

					}
				}
			}

			@Override
			public void reset() {
			}
		});
		scene.setBackground(mGrassBackground);
		backSprite = new Sprite(0, 0, background, getVertexBufferObjectManager());
		scene.attachChild(backSprite);
		
		HUD hud = new HUD();
		fireballHUD = new TiledSprite(0, 0, fireballIcon,
				getVertexBufferObjectManager());
		fireballHUD.setCurrentTileIndex(0);
		
		hud.attachChild(fireballHUD);
		mBoundChaseCamera.setHUD(hud);
		return scene;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
		quitDialog.setTitle("Выход: Вы уверены?");

		quitDialog.setPositiveButton("Таки да!", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				client.exit();
				finish();
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
	 public void animateSprite(float sX, float eX, float sY, float eY, AnimatedSprite sprite, String ID) {
		 
		 float angle =  (float) Math.atan2((eY - sY), (eX - sX));
		 int number = forNumberPosition.get(ID); 
		  if (angle > Math.PI/6 && angle < Math.PI*2/6) {
			 if (number != 2) {
				 Log.d("myLogs", "2");
				 forNumberPosition.put(ID, 2);
				sprite.animate(new long[] {200, 200, 200}, new int[] {21,22,23}, -1);
			 }
		 } else
		 if (angle > Math.PI*2/6 && angle < Math.PI*4/6) {
			 if (number != 3) {
				 Log.d("myLogs", "3");
				 forNumberPosition.put(ID, 3);
				sprite.animate(new long[] {200, 200, 200}, new int[] {6,7,8}, -1);
			 }
		 } else
		 if (angle > Math.PI*4/6 && angle < Math.PI*5/6) {
			 if (number != 4) {
				 Log.d("myLogs", "4");
				 forNumberPosition.put(ID, 4);
				sprite.animate(new long[] {200, 200, 200}, new int[] {15,16,17}, -1);
			 }
		} else
		 if (angle > Math.PI*5/6 || angle < -Math.PI*5/6) {
			 if (number != 5) {
				 Log.d("myLogs", "5");
				 forNumberPosition.put(ID, 5);
				sprite.animate(new long[] {200, 200, 200}, new int[] {0,1,2}, -1);
			 }
		 } else
		 if (angle > -Math.PI*5/6 && angle <-Math.PI*4/6) {
			 if (number != 6) {
				 forNumberPosition.put(ID, 6);
				 Log.d("myLogs", "6");
				sprite.animate(new long[] {200, 200, 200}, new int[] {12,13,14}, -1);
			 }
		 } else
		 if (angle < -Math.PI*2/6 && angle > -Math.PI*4/6) {
			 if (number != 7) {
				 Log.d("myLogs", "7");
				 forNumberPosition.put(ID, 7);
				sprite.animate(new long[] {200, 200, 200}, new int[] {9,10,11}, -1);
			 }
		 } else
		 if (angle < -Math.PI/6 && angle > -Math.PI*2/6) {
			 if (number != 8) {
				 Log.d("myLogs", "8");
				 forNumberPosition.put(ID, 8); 
				sprite.animate(new long[] {200, 200, 200}, new int[] {18,19,20}, -1);
			 }
		 } else if (angle > -Math.PI/6 && angle < Math.PI/6 && angle != 0) {
			 if (number != 1) {
				 Log.d("myLogs", "1");
				 forNumberPosition.put(ID, 1);
				sprite.animate(new long[] {200, 200, 200}, new int[] {3,4,5}, -1);
			 }
		 } 
		  
		 
	 }
}