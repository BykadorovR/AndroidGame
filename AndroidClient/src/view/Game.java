package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.FactoryModel;
import model.ICommunicationModel;
import model.Player;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
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

import android.view.Display;
import android.view.MotionEvent;
import controller.FactoryController;
import controller.IHardwareController;
import controller.IWarController;

public class Game extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	protected BitmapTextureAtlas mBitmapTextureAtlas;

	ArrayList<Player> players;
	Map<String, AnimatedSprite> sprites = new HashMap<String, AnimatedSprite>();
	Map<String, AnimatedSprite> fireballs = new HashMap<String, AnimatedSprite>();
	Map<String, Sprite> health = new HashMap<String, Sprite>();

	private TiledTextureRegion fireballIcon;
	private TextureRegion background;
	private TiledSprite fireballHUD;
	private TiledTextureRegion knight;
	private TiledTextureRegion fireball;
	private Sprite backSprite;
	private RepeatingSpriteBackground mGrassBackground;
	protected BoundCamera mBoundChaseCamera;
	Map<String, Integer> forNumberPositionAnimate = new HashMap<String, Integer>();
	private TextureRegion hpTexture;
	Display display;

	@Override
	public EngineOptions onCreateEngineOptions() {
		display = getWindowManager().getDefaultDisplay();
		mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		mBoundChaseCamera.setBoundsEnabled(true);
		mBoundChaseCamera.setBounds(-200, -200, 1400 + 200, 1200 + 200);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				mBoundChaseCamera);
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 2048, 2048,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA); // ¿ÚÎ‡Ò
		fireball = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				this.mBitmapTextureAtlas, this, "fireball.png", 0, 1201, 10, 1);
		fireballIcon = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"fireball_icon.png", 1750, 0, 2, 1);
		knight = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				this.mBitmapTextureAtlas, this, "knight.png", 1401, 0, 3, 8);
		background = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mBitmapTextureAtlas, this, "field.png", 0, 0);
		hpTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mBitmapTextureAtlas, this, "hp.png", 500, 1500);
		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH,
				CAMERA_HEIGHT, this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(),
						"gfx/lava_tile.png"),
				this.getVertexBufferObjectManager());
		mBitmapTextureAtlas.load();
	}

	Sprite yourSprite;
	Sprite notSprite;
	boolean _clickForAnimation = false;
	int counter = 0;
	private HUD hud;
	IHardwareController hardwareController;
	IWarController warController;
	ICommunicationModel multiComm;

	@Override
	protected Scene onCreateScene() {
		final Scene scene = new Scene();

		hud = new HUD();

		FactoryModel fModel = new FactoryModel();
		multiComm = fModel.createMultiplayerCommunicationModel();
		multiComm.Initialize();

		// Check max amount players in room (4) while our "player" is not exist
		// When "player" enter in the room, camera set his center as own center
		boolean _checkOurEnter = false;

		while (_checkOurEnter == false) {
			players = multiComm.getPlayers();
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i) != null)
					if (multiComm.getID().equalsIgnoreCase(
							players.get(i).getID())) {
						mBoundChaseCamera.setCenter((float) players.get(i)
								.getCoordX(), (float) players.get(i)
								.getCoordY());
						_checkOurEnter = true;
					}
			}
		}

		fireballHUD = new TiledSprite(0, 0, fireballIcon,
				getVertexBufferObjectManager());
		fireballHUD.setCurrentTileIndex(0);

		hud.attachChild(fireballHUD);
		mBoundChaseCamera.setHUD(hud);

		scene.setBackground(mGrassBackground);
		backSprite = new Sprite(0, 0, background,
				getVertexBufferObjectManager());
		scene.attachChild(backSprite);

		// Factory controller
		FactoryController fController = new FactoryController();
		// Hardware controller
		hardwareController = fController.createHardwareController(multiComm,
				this);
		hardwareController.controlExit(true);
		// War controller
		warController = fController.createWarController(multiComm, this);
		warController.controlCastFireball(true);
		warController.controlMove(true);

		//  ¿—¿Õ»≈
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
				MotionEvent localMotionEvent = pSceneTouchEvent
						.getMotionEvent();
				if (localMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					float xFromTouch = pSceneTouchEvent.getX();
					float yFromTouch = pSceneTouchEvent.getY();
					warController.controlHandle(xFromTouch, yFromTouch);
				}
				return false;
			}
		});

		FactoryView fView = new FactoryView();
		final ICastView fireballView = fView.createCastView(scene, this);
		final IPlayerView warView = fView.createWarView(scene, this);
		// View
		scene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				players = multiComm.getPlayers();

				// =====================fireball====================
				fireballView.draw(fireball, fireballHUD, multiComm);
				fireballView.move();
				fireballView.animate(fireballHUD, multiComm);
				fireballView.detach();
				// =====================player====================
				warView.detach(multiComm);
				warView.draw(knight, hpTexture, multiComm);
				warView.animate();
				warView.move(multiComm);

			}

			@Override
			public void reset() {
			}
		});

		return scene;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public Map<String, AnimatedSprite> getFireballs() {
		return fireballs;
	}

	public Map<String, AnimatedSprite> getSprites() {
		return sprites;
	}

	public Map<String, Sprite> getHealths() {
		return health;
	}

	public Map<String, Integer> getPositionAnimation() {
		return forNumberPositionAnimate;
	}

	public TiledSprite getFireball() {
		return fireballHUD;
	}

	public BoundCamera getCamera() {
		return mBoundChaseCamera;
	}

	public Display getDisplay() {
		return display;
	}

	public HUD getHUD() {
		return hud;
	}

	@Override
	public void onBackPressed() {
		hardwareController.controlHandle();
	}

}