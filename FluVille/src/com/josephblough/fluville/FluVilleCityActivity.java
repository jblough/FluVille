package com.josephblough.fluville;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectGroup;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.MathUtils;

import com.josephblough.fluville.data.SyndicatedFeed;
import com.josephblough.fluville.tasks.FluPodcastsFeedDownloaderTask;
import com.josephblough.fluville.tasks.FluUpdatesFeedDownloaderTask;
import com.josephblough.fluville.tasks.SyndicatedFeedDownloaderTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FluVilleCityActivity extends BaseGameActivity implements IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "FluVilleCityActivity";
	
	public static final int CAMERA_WIDTH = 760;
	public static final int CAMERA_HEIGHT = 480;
	
	//public static final int CAMERA_WIDTH = 720;
	//public static final int CAMERA_HEIGHT = 480;
	
	//public static final int CAMERA_WIDTH = 380;
	//public static final int CAMERA_HEIGHT = 240;
	
	//public static final int CAMERA_WIDTH = 480;
	//public static final int CAMERA_HEIGHT = 320;
	
	public static final float SECONDS_PER_FLUVILLE_HOUR = 2.0f;
	private static final int DAYS_BETWEEN_WEEKLY_NEWS = 7;
	
	public static final String MAP_LANDMARK_LEFT_RESIDENTIAL_INTERSECTION = "TopLeftResidentialCorner";
	public static final String MAP_LANDMARK_RIGHT_RESIDENTIAL_INTERSECTION = "TopRightResidentialCorner";
	public static final String MAP_LANDMARK_RESIDENTIAL_SPAWN_POINT_1 = "SpawnPoint1";
	public static final String MAP_LANDMARK_RESIDENTIAL_SPAWN_POINT_2 = "SpawnPoint2";
	public static final String MAP_LANDMARK_FLYER_1 = "Flyers1";
	public static final String MAP_LANDMARK_FLYER_2 = "Flyers2";
	public static final String MAP_LANDMARK_TOP_LEFT_INTERSECTION = "TopLeftIntersection";
	public static final String MAP_LANDMARK_TOP_RIGHT_INTERSECTION = "TopRightIntersection";
	public static final String MAP_LANDMARK_MIDDLE_LEFT_INTERSECTION = "MiddleLeftIntersection";
	public static final String MAP_LANDMARK_MIDDLE_RIGHT_INTERSECTION = "MiddleRightIntersection";
	public static final String MAP_LANDMARK_BOTTOM_LEFT_INTERSECTION = "BottomLeftIntersection";
	public static final String MAP_LANDMARK_BOTTOM_RIGHT_INTERSECTION = "BottomRightIntersection";

	public static final String MAP_LANDMARK_HOSPITAL = "Hospital";
	public static final String MAP_LANDMARK_SCHOOL = "School";
	public static final String MAP_LANDMARK_PIZZA = "Pizza";
	public static final String MAP_LANDMARK_STORE_1 = "Store1";
	public static final String MAP_LANDMARK_STORE_2 = "Store2";
	public static final String MAP_LANDMARK_OFFICE_1 = "Office1";
	public static final String MAP_LANDMARK_OFFICE_2 = "Office2";
	public static final String MAP_LANDMARK_OFFICE_3 = "Office3";
	public static final String MAP_LANDMARK_OFFICE_4 = "Office4";
	
	public static final String MAP_LANDMARK_HOSPITAL_BLDG = "HospitalBuilding";
	public static final String MAP_LANDMARK_SCHOOL_BLDG = "SchoolBuilding";
	public static final String MAP_LANDMARK_PIZZA_BLDG = "PizzaBuilding";
	public static final String MAP_LANDMARK_STORE_1_BLDG = "StoreBuilding1";
	public static final String MAP_LANDMARK_STORE_2_BLDG = "StoreBuilding2";
	public static final String MAP_LANDMARK_OFFICE_1_BLDG = "OfficeBuilding1";
	public static final String MAP_LANDMARK_OFFICE_2_BLDG = "OfficeBuilding2";
	public static final String MAP_LANDMARK_OFFICE_3_BLDG = "OfficeBuilding3";
	public static final String MAP_LANDMARK_OFFICE_4_BLDG = "OfficeBuilding4";
	
	// ===========================================================
	// Fields
	// ===========================================================

	//private ZoomCamera mBoundChaseCamera;
	private Camera mBoundChaseCamera;

	private Texture mPlayerTexture;
	public TiledTextureRegion mPlayerTextureRegion;
	private Texture mInfectedPlayerTexture;
	public TiledTextureRegion mInfectedPlayerTextureRegion;
	private Texture mImmunizedPlayerTexture;
	public TiledTextureRegion mImmunizedPlayerTextureRegion;
	
	public Texture mArrowTexture;
	public TextureRegion mArrowUpTextureRegion;
	public TextureRegion mArrowDownTextureRegion;
	
	public Texture mSpeechBubbleTexture;
	public TextureRegion mSpeechBubble;
	
	public TMXTiledMap mTMXTiledMap;
	private HashMap<String, TMXObject> mapObjects = new HashMap<String, TMXObject>();
	
	private Texture mMenuFontTexture;
	public Font mMenuFont;
	private Texture mDialogFontTexture;
	private Font mDialogFont;
	
	private Texture mMenuItemsTexture;
	public TextureRegion mImmunizationTextureRegion;
	public TextureRegion mSanitizerTextureRegion;
	public TextureRegion mSpongeTextureRegion;
	public TextureRegion mSendHomeTextureRegion;

	public GameState gameState;
	
	private Map<String, Rectangle> infectedBuildingMap = new ConcurrentHashMap<String, Rectangle>();
	
	public static RectanglePool RECTANGLE_POOL = new RectanglePool();
	
	private Integer lastWeeklyNewsFeed = null;
	private boolean cdcFeedsReadyNotificationSent = false;
	private boolean cdcFeedsReady = false;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		displayDisclaimer();
		
		//this.mBoundChaseCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mBoundChaseCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera));
	}

	@Override
	public void onLoadResources() {
		// Tell Android that the volume control buttons should set the
		//	media volume and not the ringer volume
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
      
		this.mPlayerTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mPlayerTexture, this, "gfx/rpg_sprite_walk.png", 0, 0, 8, 4); // 72x128

		this.mInfectedPlayerTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mInfectedPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mInfectedPlayerTexture, this, "gfx/infected_sprite_walk.png", 0, 0, 8, 4); // 72x128

		this.mImmunizedPlayerTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mImmunizedPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mImmunizedPlayerTexture, this, "gfx/immunized_sprite_walk.png", 0, 0, 8, 4); // 72x128
		
		this.mEngine.getTextureManager().loadTextures(this.mPlayerTexture, this.mInfectedPlayerTexture, this.mImmunizedPlayerTexture);
		
		this.mArrowTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mArrowDownTextureRegion = TextureRegionFactory.createFromAsset(this.mArrowTexture, this, "gfx/blue_arrow_down.png", 0, 0); // 72x128
		this.mArrowUpTextureRegion = TextureRegionFactory.createFromAsset(this.mArrowTexture, this, "gfx/blue_arrow_up.png", 0, 32); // 72x128
		this.mEngine.getTextureManager().loadTexture(this.mArrowTexture);
		
		this.mSpeechBubbleTexture = new Texture(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mSpeechBubble = TextureRegionFactory.createFromAsset(mSpeechBubbleTexture, this, "gfx/speech_bubble.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mSpeechBubbleTexture);
		
		this.mMenuItemsTexture = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mImmunizationTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/syringe2.png", 0, 0);
		this.mSanitizerTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/sanitizer.png", 0, 32);
		this.mSpongeTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/sponge.png", 0, 64);
		this.mSendHomeTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/ic_launcher_home.png", 0, 96);
		this.mEngine.getTextureManager().loadTexture(this.mMenuItemsTexture);

		
		this.mMenuFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mMenuFont = new Font(this.mMenuFontTexture, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD), 12, false, Color.WHITE);
		this.mMenuFont = new Font(this.mMenuFontTexture, Typeface.create(Typeface.SERIF, Typeface.NORMAL), 12, true, Color.WHITE);

		this.mDialogFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mMenuFont = new Font(this.mDialogFontTexture, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD), 12, false, Color.WHITE);
		//this.mDialogFont = new Font(this.mDialogFontTexture, Typeface.create(Typeface.SERIF, Typeface.NORMAL), 20, true, Color.BLACK);
		this.mDialogFont = new Font(this.mDialogFontTexture, Typeface.create(Typeface.SERIF, Typeface.NORMAL), 24, true, Color.BLACK);
		
		this.mEngine.getTextureManager().loadTextures(this.mMenuFontTexture, this.mDialogFontTexture);
		this.mEngine.getFontManager().loadFonts(this.mMenuFont, this.mDialogFont);
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(2);
		scene.setOnAreaTouchTraversalFrontToBack();

		try {
			final TMXLoader tmxLoader = new TMXLoader(this, this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/smaller_tiled.tmx");
		} catch (final TMXLoadException tmxle) {
			Log.e(TAG, tmxle.getMessage(), tmxle);
		}

		for (TMXLayer tmxLayer : this.mTMXTiledMap.getTMXLayers()) {
			scene.getLastChild().attachChild(tmxLayer);
		}

		for (TMXObjectGroup group : this.mTMXTiledMap.getTMXObjectGroups()) {
			for (TMXObject object : group.getTMXObjects()) {
				//Log.d(TAG, "Object: " + object.getName() + " at (" + object.getX() + ", " + object.getY() + ")");
				mapObjects.put(object.getName(), object);
			}
		}

		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingEnabled(true);

		gameState = new GameState();
		
		HUD hud = new FluVilleCityHUD(this);
		this.mBoundChaseCamera.setHUD(hud);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
		Log.d(TAG, "onLoadComplete");
		for (int i=0; i<5; i++) {
			addResident();
			
			if (i < 1) {
				gameState.residents.get(gameState.residents.size() - 1).infect();
			}
		}

		recordBeginningOfDayStats();
		
		// Update handler to check if more residents are infected
		this.mEngine.registerUpdateHandler(new TimerHandler(1.0f, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (gameState.stateOfPlay == GameState.STATE_OF_PLAY_RUNNING) {
					checkForMoreInfections();

					((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateInfectionRateLabels();
				}
			}
		}));
		
		// Update handler to handle time passing
		this.mEngine.registerUpdateHandler(new TimerHandler(SECONDS_PER_FLUVILLE_HOUR, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (gameState.stateOfPlay == GameState.STATE_OF_PLAY_RUNNING) {
					if (gameState.hourOfDay < 23) {
						incrementHour();
					}
					else {
						recordEndOfDayStats();
						incrementDay();
					}
					
					((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateClock();
				}
			}
		}));
		
		loadFeeds();

		retrievePreviouslyShownMessages();
		
		if (!gameState.shownWelcomeMessage) {
			displayMessage(getString(R.string.welcome), getString(R.string.control_instructions));
			gameState.shownWelcomeMessage = true;
			updatePreviouslyShownMessages("shownWelcomeMessage");
		}
	}
	
	private void incrementHour() {
		gameState.hourOfDay++;

		final TMXObject pizza = findLandmark(MAP_LANDMARK_PIZZA);
		final boolean isLunchtime = gameState.hourOfDay <=1 && gameState.hourOfDay >= 11;
		for (final FluVilleResident resident : gameState.residents) {
			// Decrease any accumulations that we're keeping track of for the resident
			if (resident.hoursOfSanitizerRemaining > 0) {
				resident.reduceSanitizerProtection();
			}
			else {
				resident.removeSanitizerProtection();
			}

			// Get the residents walking again
			if (!resident.isWalking && !resident.wasSentHomeSick && isLunchtime && 
					!resident.currentDestination.getName().equals(MAP_LANDMARK_PIZZA)) {
				// Go to lunch
				this.mEngine.registerUpdateHandler(new TimerHandler(MathUtils.random(0.1f, 1.9f), new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						mEngine.getScene().getLastChild().attachChild(resident);
						resident.walk(pizza);
					}
				}));
			}
			if (!resident.isWalking && !resident.isAtWork() && 
					!resident.wasSentHomeSick && 
					(!resident.infected ||				// non-infected residents code right out
					(MathUtils.random(0, 3) == 0))) {	// infected residents don't come out all at once
				// Go to work
				this.mEngine.registerUpdateHandler(new TimerHandler(MathUtils.random(0.1f, 1.9f), new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						mEngine.getScene().getLastChild().attachChild(resident);
						resident.walk(resident.placeOfWork);
						
						if (!gameState.shownInfectedPersonMessage && resident.infected) {
							// Set a delay so the infected resident is clearly visible on the screen
							mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
								@Override
								public void onTimePassed(TimerHandler pTimerHandler) {
									displayInfectedPersonWarning(resident);
								}
							}));
						}
					}
				}));
			}
			else if (!resident.isWalking && resident.isAtWork() && (MathUtils.random(0, 2) == 0)) {
				// Go home
				this.mEngine.registerUpdateHandler(new TimerHandler(MathUtils.random(0.1f, 1.9f), new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						mEngine.getScene().getLastChild().attachChild(resident);
						resident.walk(resident.home);
					}
				}));
			}
		}
	}
	
	private void incrementDay() {
		gameState.day++;
		gameState.hourOfDay = 0;

		for (FluVilleResident resident : gameState.residents) {
			// Decrease any accumulations that we're keeping track of for the resident
			if (resident.daysOfInfectionRemaining > 0) {
				resident.daysOfInfectionRemaining--;
				if (resident.daysOfInfectionRemaining <= 0) {
					resident.recover();
				}
			}
			
			// Reset sanitizer at the beginning of each day
			//resident.hoursOfSanitizerRemaining = 0;
			if (resident.hoursOfSanitizerRemaining > 0) {
				resident.reduceSanitizerProtection();
			}
			else {
				resident.removeSanitizerProtection();
			}
			
			// Reset sent home sick flag
			resident.wasSentHomeSick = false;
		}
		
		recordBeginningOfDayStats();
		
		// Increase flu shot vaccines, hand sanitizer doses available when appropriate
		if (gameState.day % GameState.DAYS_BETWEEN_FLU_SHOT_REFILLS == 0) {
			gameState.immunizationsRemaining = 
				Math.min(gameState.immunizationsRemaining + GameState.FLU_SHOT_REFILL_SIZE, GameState.MAX_FLU_SHOT_DOSES);
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateFluShotsLabel();
		}
		if (gameState.day % GameState.DAYS_BETWEEN_HAND_SANITIZER_REFILLS == 0) {
			gameState.handSanitizerDosesRemaining = 
				Math.min(gameState.handSanitizerDosesRemaining + GameState.HAND_SANITIZER_REFILL_SIZE, GameState.MAX_HAND_SANITIZER_DOSES);
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateHandSanitizersLabel();
		}

		// Add more residents
		// Randomly infect 1 resident * the day number (up to 5)
		for (int i=0; i<5; i++) {
			addResident();
			
			if (i < 1) {
				gameState.residents.get(gameState.residents.size() - 1).infect();
			}
		}

		((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateImmunizationRateLabels();
		((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateInfectionRateLabels();

		if (!cdcFeedsReadyNotificationSent && cdcFeedsReady) {
			notifyFeedsReady();
		}

		// On the first day of each week, display CDC news feeds
		if (gameState.day % DAYS_BETWEEN_WEEKLY_NEWS == 0) {
			displayWeeklyNews();
		}
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			for (FluVilleResident resident : gameState.residents) {
				if (resident.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
					//Log.d(TAG, "Tapped on a resident");
					switch (((FluVilleCityHUD)mBoundChaseCamera.getHUD()).currentMenuSelection) {
					case FluVilleCityHUD.HUD_MENU_IMMUNIZATION:
						immunizeResident(resident);
						break;
					case FluVilleCityHUD.HUD_MENU_SANITIZER:
						sanitizeResident(resident);
						break;
					case FluVilleCityHUD.HUD_MENU_GRAB_RESIDENTS:
						sendResidentHome(resident);
						break;
					}
					return true;
				}
			}
			
			// Clean infected buildings
			if (((FluVilleCityHUD)mBoundChaseCamera.getHUD()).currentMenuSelection == FluVilleCityHUD.HUD_MENU_SPONGE) {
				// Iterate through the infected landmarks and clean if we're moving over them
				for (Entry<String, Rectangle> buildingEntry : infectedBuildingMap.entrySet()) {
					Rectangle buildingRectangle = buildingEntry.getValue();
					if (buildingRectangle.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
						cleanBuilding(findLandmark(buildingEntry.getKey()));
						return true;
					}
				}
			}
			
			// Check if the flyers were tapped
			TMXObject flyer = findLandmark(MAP_LANDMARK_FLYER_1);
			if (flyer != null) {
				Rectangle flyerRectangle = RECTANGLE_POOL.obtain(flyer.getX(), flyer.getY(), flyer.getWidth(), flyer.getHeight());
				boolean collides = flyerRectangle.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				RECTANGLE_POOL.recyclePoolItem(flyerRectangle);
				if (collides) {
					displayNews();
					return true;
				}
			}
			
			flyer = findLandmark(MAP_LANDMARK_FLYER_2);
			if (flyer != null) {
				Rectangle flyerRectangle = RECTANGLE_POOL.obtain(flyer.getX(), flyer.getY(), flyer.getWidth(), flyer.getHeight());
				boolean collides = flyerRectangle.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				RECTANGLE_POOL.recyclePoolItem(flyerRectangle);
				if (collides) {
					displayNews();
					return true;
				}
			}
		}
		else if (pSceneTouchEvent.isActionMove()) {
			if (((FluVilleCityHUD)mBoundChaseCamera.getHUD()).currentMenuSelection == FluVilleCityHUD.HUD_MENU_SPONGE) {
				// Iterate through the infected landmarks and clean if we're moving over them
				for (Entry<String, Rectangle> buildingEntry : infectedBuildingMap.entrySet()) {
					Rectangle buildingRectangle = buildingEntry.getValue();
					if (buildingRectangle.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
						cleanBuilding(findLandmark(buildingEntry.getKey()));
						return true;
					}
				}
			}
		}

		return false;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void immunizeResident(final FluVilleResident resident) {
		if (!resident.immunized && !resident.infected && gameState.immunizationsRemaining > 0) {
			resident.immunize();
			gameState.immunizationsRemaining--;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateFluShotsLabel();
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateImmunizationRateLabels();
			
			if (!gameState.shownLimitedSuppliesMessage && gameState.immunizationsRemaining == 0) {
				displayLimitedSuppliesMessage();
			}
		}
	}
	
	public void sanitizeResident(final FluVilleResident resident) {
		if (!resident.immunized && !resident.infected && resident.hoursOfSanitizerRemaining <= 0 && 
				gameState.handSanitizerDosesRemaining > 0) {
			resident.applyHandSanitizer();
			gameState.handSanitizerDosesRemaining--;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateHandSanitizersLabel();
			
			if (!gameState.shownLimitedSuppliesMessage && gameState.handSanitizerDosesRemaining == 0) {
				displayLimitedSuppliesMessage();
			}
		}
	}
	
	public void sendResidentHome(final FluVilleResident resident) {
		//Log.d(TAG, "Sending a resident home sick");
		resident.sendHome();
	}
	
	public void addResident() {
		addResident(this.mEngine.getScene());
	}
	
	private void addResident(final Scene scene) {
		final TMXObject spawnPoint = getRandomOrigin();
		final FluVilleResident player = new FluVilleResident(this, scene, spawnPoint, this.mPlayerTextureRegion);

		player.setPlaceOfWork(getRandomDestination());
		
		//scene.getLastChild().attachChild(player);
		gameState.residents.add(player);
	}

	public TMXObject findLandmark(final String landmark) {
		return mapObjects.get(landmark);
	}
	
	private TMXObject getRandomOrigin() {
		if (MathUtils.random(0, 1) == 0) 
			return mapObjects.get(MAP_LANDMARK_RESIDENTIAL_SPAWN_POINT_1);
		else
			return mapObjects.get(MAP_LANDMARK_RESIDENTIAL_SPAWN_POINT_2);
	}
	
	private TMXObject getRandomDestination() {
		switch (MathUtils.random(0, 8)) {
		case 0:
			return mapObjects.get(MAP_LANDMARK_PIZZA);
		case 1:
			return mapObjects.get(MAP_LANDMARK_OFFICE_1);
		case 2:
			return mapObjects.get(MAP_LANDMARK_OFFICE_2);
		case 3:
			return mapObjects.get(MAP_LANDMARK_OFFICE_3);
		case 4:
			return mapObjects.get(MAP_LANDMARK_OFFICE_4);
		case 5:
			return mapObjects.get(MAP_LANDMARK_STORE_1);
		case 6:
			return mapObjects.get(MAP_LANDMARK_STORE_2);
		case 7:
			return mapObjects.get(MAP_LANDMARK_HOSPITAL);
		case 8:
			return mapObjects.get(MAP_LANDMARK_SCHOOL);
		}
		return null;
	}

	public int getMapWidth() {
		return this.mTMXTiledMap.getTileColumns() * this.mTMXTiledMap.getTileWidth();
	}
	
	private void displayMessage(final String... messages) {
		gameState.stateOfPlay = GameState.STATE_OF_PLAY_PAUSED;
		final Scene messageScene = new Scene(1);
		messageScene.setBackgroundEnabled(false);
		final Sprite bubble = new Sprite(0, 0, mSpeechBubble);
		final Text text = new Text(0.0f, 0.0f, FluVilleCityActivity.this.mDialogFont, messages[0]);
		bubble.setWidth(text.getWidth() + 25.0f);
		bubble.setHeight(text.getHeight() + 125.0f);
		
		// Center the text
		text.setPosition((bubble.getWidth() - text.getWidth()) / 2, 25.0f/*(bubble.getHeight() - text.getHeight()) / 2*/);
		bubble.attachChild(text);
		
		// Bottom left corner
		bubble.setPosition(0.0f, CAMERA_HEIGHT - bubble.getHeight());
		messageScene.attachChild(bubble);
		mEngine.getScene().setChildScene(messageScene, false, true, true);

		// Keep the dialog up for at least a second before registering a removal handler
		this.mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				messageScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
					
					@Override
					public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
						//Log.d(TAG, "messageScene onSceneTouchEvent");
						mEngine.getScene().clearChildScene();
						if (messages.length > 1) {
							String[] nextMessages = new String[messages.length-1];
							for (int i=1; i<messages.length; i++) {
								nextMessages[i-1] = messages[i];
							}
							displayMessage(nextMessages);
						}
						else {
							gameState.stateOfPlay = GameState.STATE_OF_PLAY_RUNNING;
						}
						return true;
					}
				});
			}
		}));
	}

	public void showArrow(final float pX, final float pY, final boolean down) {
		final Sprite arrow = new Sprite(pX, pY, (down) ? mArrowDownTextureRegion : mArrowUpTextureRegion);
		final float pY2 = (down) ? (pY + 10.0f) : (pY - 10.0f);
		
		SequenceEntityModifier modifier = new SequenceEntityModifier(
					new MoveYModifier(0.5f, pY, pY2),
					new MoveYModifier(0.5f, pY2, pY),
					new MoveYModifier(0.5f, pY, pY2),
					new MoveYModifier(0.5f, pY2, pY),
					new MoveYModifier(0.5f, pY, pY2),
					new MoveYModifier(0.5f, pY2, pY),
					new MoveYModifier(0.5f, pY, pY2),
					new MoveYModifier(0.5f, pY2, pY)
					);
		
		arrow.registerEntityModifier(modifier);
		this.mEngine.getScene().getLastChild().attachChild(arrow);

		this.mEngine.registerUpdateHandler(new TimerHandler(4.0f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				arrow.setVisible(false);
				mEngine.getScene().getLastChild().detachChild(arrow);
			}
		}));
	}
	
	public void displayDisclaimer() {
		final String disclaimerSetting = "shownDisclaimer";
		final SharedPreferences preferences = this.getSharedPreferences(getClass().getName(), 0);
		final boolean shownDisclaimer = preferences.getBoolean(disclaimerSetting, false);
		if (!shownDisclaimer) {
			AlertDialog.Builder alert = new AlertDialog.Builder(FluVilleCityActivity.this);
			alert.setTitle("Disclaimer");
			alert.setCancelable(false);
			alert.setMessage(getString(R.string.disclaimer)).
			setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean(disclaimerSetting, true);
					editor.commit();
				}
			}).
			setNegativeButton("Decline", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			});
			alert.show();
		}
	}
	
	public void retrievePreviouslyShownMessages() {
		final SharedPreferences preferences = this.getSharedPreferences(getClass().getName(), 0);
		gameState.shownWelcomeMessage = preferences.getBoolean("shownWelcomeMessage", false);
		if (gameState.shownWelcomeMessage) {
			gameState.stateOfPlay = GameState.STATE_OF_PLAY_PAUSED;
			mEngine.stop();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					AlertDialog.Builder alert = new AlertDialog.Builder(FluVilleCityActivity.this);
					alert.setTitle("Skip messages");
					alert.setCancelable(false);
					alert.setMessage(getString(R.string.skip_messages_already_viewed))
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											// Load message viewing state
											gameState.shownImmunizationMessage = preferences.getBoolean("shownImmunizationMessage", false);
											gameState.shownSanitizerMessage = preferences.getBoolean("shownSanitizerMessage", false);
											gameState.shownSpongeMessage = preferences.getBoolean("shownSpongeMessage", false);
											gameState.shownSendHomeMessage = preferences.getBoolean("shownSendHomeMessage", false);
											gameState.shownInfectedPersonMessage = preferences.getBoolean("shownInfectedPersonMessage", false);
											gameState.shownInfectedBuildingMessage = preferences.getBoolean("shownInfectedBuildingMessage", false);
											gameState.shownLimitedSuppliesMessage = preferences.getBoolean("shownLimitedSuppliesMessage", false);
											gameState.stateOfPlay = GameState.STATE_OF_PLAY_RUNNING;
											mEngine.start();
										}
									})
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									// Show the welcome message
									gameState.stateOfPlay = GameState.STATE_OF_PLAY_RUNNING;
									mEngine.start();
									displayMessage(getString(R.string.welcome), getString(R.string.control_instructions));
								}
							});
					alert.show();
				}
			});
		}
	}
	
	public void updatePreviouslyShownMessages(final String messageToUpdate) {
		SharedPreferences preferences = this.getSharedPreferences(getClass().getName(), 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(messageToUpdate, true);
		editor.commit();
	}
	
	public void updatePreviouslyShownMessages() {
		SharedPreferences preferences = this.getSharedPreferences(getClass().getName(), 0);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean("shownWelcomeMessage", gameState.shownWelcomeMessage);
		editor.putBoolean("shownImmunizationMessage", gameState.shownImmunizationMessage);
		editor.putBoolean("shownSanitizerMessage", gameState.shownSanitizerMessage);
		editor.putBoolean("shownSpongeMessage", gameState.shownSpongeMessage);
		editor.putBoolean("shownSendHomeMessage", gameState.shownSendHomeMessage);
		editor.putBoolean("shownInfectedPersonMessage", gameState.shownInfectedPersonMessage);
		editor.putBoolean("shownInfectedBuildingMessage", gameState.shownInfectedBuildingMessage);
		editor.putBoolean("shownLimitedSuppliesMessage", gameState.shownLimitedSuppliesMessage);
	}
	
	public void displayImmunizationBenefits() {
		//Log.d(TAG, "displayImmunizationBenefits");
		displayMessage(getString(R.string.immunization_benefits), getString(R.string.immunization_instructions));
		gameState.shownImmunizationMessage = true;
		updatePreviouslyShownMessages("shownImmunizationMessage");
	}

	public void displayHandSanitizerBenefits() {
		//Log.d(TAG, "displayHandSanitizerBenefits");
		displayMessage(getString(R.string.sanitizer_benefits), getString(R.string.sanitizer_instructions));
		gameState.shownSanitizerMessage = true;
		updatePreviouslyShownMessages("shownSanitizerMessage");
	}

	public void displaySpongeUsage() {
		//Log.d(TAG, "displaySpongeUsage");
		displayMessage(getString(R.string.sponge_usage), getString(R.string.sponge_instructions));
		gameState.shownSpongeMessage = true;
		updatePreviouslyShownMessages("shownSpongeMessage");
	}
	
	public void displaySendResidentHomeUsage() {
		//Log.d(TAG, "displaySendResidentHomeUsage");
		displayMessage(getString(R.string.send_residents_home_reason), getString(R.string.send_residents_home_instructions));
		gameState.shownSendHomeMessage = true;
		updatePreviouslyShownMessages("shownSendHomeMessage");
	}
	
	public void displayInfectedPersonWarning(final FluVilleResident resident) {
		//Log.d(TAG, "displayInfectedPersonWarning");
		showArrow(resident.getX(), (resident.getY() + resident.getHeight()), false);
		displayMessage(getString(R.string.infected_person_warning), getString(R.string.infected_person_instructions));
		gameState.shownInfectedPersonMessage = true;
		updatePreviouslyShownMessages("shownInfectedPersonMessage");
	}
	
	public void displayInfectedBuildingWarning(final TMXObject building) {
		showArrow(building.getX(), building.getY() - building.getWidth(), true);
		displayMessage(getString(R.string.infected_building_warning), getString(R.string.infected_building_instructions));
		gameState.shownInfectedBuildingMessage = true;
		updatePreviouslyShownMessages("shownInfectedBuildingMessage");
	}
	
	public void displayLimitedSuppliesMessage() {
		displayMessage(getString(R.string.limited_supplies_warning));
		gameState.shownLimitedSuppliesMessage = true;
		updatePreviouslyShownMessages("shownLimitedSuppliesMessage");
	}
	
	public void displayNews() {
		gameState.stateOfPlay = GameState.STATE_OF_PLAY_PAUSED;
		mEngine.stop();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final ProgressDialog dialog = ProgressDialog.show(FluVilleCityActivity.this, "", "Loading Feeds...");
				new Thread() {

					public void run() {
						try {
							sleep(1000);
						}
						catch (Exception e) {
							
						}
						dialog.dismiss();
					    Intent i = new Intent(FluVilleCityActivity.this, FeedsTabActivity.class);
					    startActivityForResult(i, 0);
					}
				}.start();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		
		gameState.stateOfPlay = GameState.STATE_OF_PLAY_RUNNING;
		if (!mEngine.isRunning()) {
			mEngine.start();
		}
	}
	
	public void infectBuilding(final TMXObject destination) {
		TMXObject building = getBuildingForDestination(destination.getName());
		if (building != null && !this.infectedBuildingMap.containsKey(building.getName())) {
			Rectangle buildingRectangle = RECTANGLE_POOL.obtain(building.getX(), building.getY(), building.getWidth(), building.getHeight());
			buildingRectangle.setColor(1.0f, 0.0f, 0.0f, 0.30f);
			this.infectedBuildingMap.put(building.getName(), buildingRectangle);
			mEngine.getScene().getLastChild().attachChild(buildingRectangle);
			if (!gameState.shownInfectedBuildingMessage)
				displayInfectedBuildingWarning(building);
			
			// All of the residents currently in the building become infected
			Set<FluVilleResident> residents = getSusceptibleResidents();
			for (FluVilleResident resident : residents) {
				if (resident.currentDestination != null && 
						resident.currentDestination.getName().equals(building.getName()) && // Is the building, they're destination?
						!resident.isWalking && // The resident isn't walking if they're already in the building
						resident.collidesWith(buildingRectangle)) { // Final check to make sure that they're in the building
					resident.infect();
				}
			}
		}
	}
		
	public void cleanBuilding(final TMXObject building) {
		runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				Rectangle buildingRectangle = infectedBuildingMap.get(building.getName());
				if (buildingRectangle != null) {
					mEngine.getScene().getLastChild().detachChild(buildingRectangle);
					RECTANGLE_POOL.recyclePoolItem(buildingRectangle);
				}
				infectedBuildingMap.remove(building.getName());
			}
		});
	}
	
	private TMXObject getBuildingForDestination(final String destinationName) {
		if (MAP_LANDMARK_HOSPITAL.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_HOSPITAL_BLDG);
		}
		else if (MAP_LANDMARK_OFFICE_1.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_OFFICE_1_BLDG);
		}
		else if (MAP_LANDMARK_OFFICE_2.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_OFFICE_2_BLDG);
		}
		else if (MAP_LANDMARK_OFFICE_3.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_OFFICE_3_BLDG);
		}
		else if (MAP_LANDMARK_OFFICE_4.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_OFFICE_4_BLDG);
		}
		else if (MAP_LANDMARK_PIZZA.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_PIZZA_BLDG);
		}
		else if (MAP_LANDMARK_STORE_1.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_STORE_1_BLDG);
		}
		else if (MAP_LANDMARK_STORE_2.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_STORE_2_BLDG);
		}
		else if (MAP_LANDMARK_SCHOOL.equals(destinationName)) {
			return findLandmark(MAP_LANDMARK_SCHOOL_BLDG);
		}
		return null;
	}
	
	public boolean isBuildingInfected(final TMXObject destination) {
		TMXObject building = getBuildingForDestination(destination.getName());
		if (building != null) {
			return infectedBuildingMap.containsKey(building.getName());
		}
		return false;
	}
	
	public synchronized void checkForMoreInfections() {
		// Iterate through all residents that are on the screen and see if they're 
		//	infected and around any other residents
		Set<FluVilleResident> residentsInProximity = new HashSet<FluVilleResident>();
		Set<FluVilleResident> susceptibleResidents = getSusceptibleResidents();
		Set<FluVilleResident> infectedResidents = getVisibleInfectedResidents();
		for (FluVilleResident resident : infectedResidents) {
			// Loop through all susceptible residents
			for (FluVilleResident otherResident : susceptibleResidents) {
				if (otherResident.isVisible() && otherResident.collidesWith(resident)) {
					residentsInProximity.add(otherResident);
				}
			}
		}
		
		// Increase the exposure for any susceptible residents that were too close to infectd resident
		for (FluVilleResident residentWithIncreasedExposure : residentsInProximity) {
			residentWithIncreasedExposure.cyclesAroundInfectedPerson++;
			// Infect residents that went over their limit
			if (residentWithIncreasedExposure.cyclesAroundInfectedPerson >= FluVilleResident.CYCLES_BEFORE_INFECTION) {
				residentWithIncreasedExposure.infect();
			}
		}
	}

	public int getImmunizedResidentCount() {
		int count = 0;
		for (FluVilleResident resident : gameState.residents) {
			if (resident.immunized)
				count++;
		}
		return count;
	}

	public int getInfectedResidentCount() {
		int count = 0;
		for (FluVilleResident resident : gameState.residents) {
			if (resident.infected)
				count++;
		}
		return count;
	}

	private Set<FluVilleResident> getVisibleInfectedResidents() {
		Set<FluVilleResident> infectedResidents = new HashSet<FluVilleResident>();
		for (FluVilleResident resident : gameState.residents) {
			if (resident.infected && resident.isVisible() && !resident.wasSentHomeSick) {
				infectedResidents.add(resident);
			}
		}
		return infectedResidents;
	}
	
	private Set<FluVilleResident> getSusceptibleResidents() {
		Set<FluVilleResident> susceptibleResidents = new HashSet<FluVilleResident>();
		for (FluVilleResident resident : gameState.residents) {
			if (!resident.infected && !resident.immunized && 
					resident.hoursOfSanitizerRemaining < 1) {
				susceptibleResidents.add(resident);
			}
		}
		return susceptibleResidents;
	}
	
	private void loadFeeds() {
		ApplicationController app = (ApplicationController)getApplicationContext();
		new FluUpdatesFeedDownloaderTask(app, this).execute();
		new FluPodcastsFeedDownloaderTask(app, this).execute();
		new SyndicatedFeedDownloaderTask(app, this, SyndicatedFeed.FLU_PAGES_TOPIC_ID).execute();
		new SyndicatedFeedDownloaderTask(app, this, SyndicatedFeed.CDC_PAGES_TOPIC_ID).execute();
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if ((pKeyCode == KeyEvent.KEYCODE_BACK || pKeyCode == KeyEvent.KEYCODE_HOME)
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			showExitConfirmationDialog();
			return true;
		}
		else if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			displayNews();
			return true;
		}
		
		return super.onKeyDown(pKeyCode, pEvent);
	}
	
	private void showExitConfirmationDialog() {
		gameState.stateOfPlay = GameState.STATE_OF_PLAY_PAUSED;
		mEngine.stop();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(FluVilleCityActivity.this);
				alert.setCancelable(false);
				alert.setTitle("Exit Confirmation");
				alert.setMessage("Are you sure you want to exit?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										finish();
									}
								})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								gameState.stateOfPlay = GameState.STATE_OF_PLAY_RUNNING;
								mEngine.start();
							}
						});
				alert.show();
			}
		});
	}
	
	private void recordBeginningOfDayStats() {
		DaySummary summary = gameState.getDaySummary();
		summary.beginningOfDayImmunizedResidents = getImmunizedResidentCount();
		summary.beginningOfDayInfectedResidents = getInfectedResidentCount();
		summary.beginningOfDayTotalResidents = gameState.residents.size();
		gameState.daySummaries.put(gameState.day, summary);
	}
	
	private void recordEndOfDayStats() {
		DaySummary summary = gameState.getDaySummary();
		summary.endOfDayImmunizedResidents = getImmunizedResidentCount();
		summary.endOfDayInfectedResidents = getInfectedResidentCount();
		summary.endOfDayTotalResidents = gameState.residents.size();
		gameState.daySummaries.put(gameState.day, summary);
	}

	public void displayWeeklyNews() {
		final Integer feed = getNextFeed();
		if (feed != null) {
			gameState.stateOfPlay = GameState.STATE_OF_PLAY_PAUSED;
			mEngine.stop();
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					final ProgressDialog dialog = ProgressDialog.show(FluVilleCityActivity.this, "", "Loading Weeky News...");
					new Thread() {

						public void run() {
							try {
								sleep(1000);
							}
							catch (Exception e) {
								
							}
							dialog.dismiss();
							if (FeedActivity.FLU_PODCASTS == feed) {
								Intent intent = new Intent(FluVilleCityActivity.this, FluPodcasts.class);
								startActivityForResult(intent, 0);
							}
							else {
								Intent intent = new Intent(FluVilleCityActivity.this, FeedActivity.class);
								intent.putExtra(FeedActivity.FEED_EXTRA, feed);
								startActivityForResult(intent, 0);
							}
						}
					}.start();
				}
			});
		}
	}
	
	private Integer getNextFeed() {
		ApplicationController app = (ApplicationController)getApplicationContext();
		if (app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) != null && 
				(lastWeeklyNewsFeed == null || lastWeeklyNewsFeed.intValue() == FeedActivity.CDC_FEATURE_PAGES)) {
			lastWeeklyNewsFeed = FeedActivity.FLU_PAGES;
			return lastWeeklyNewsFeed;
		}
		if (app.fluUpdatesFeed != null && lastWeeklyNewsFeed != null && 
				lastWeeklyNewsFeed.intValue() == FeedActivity.FLU_PAGES) {
			lastWeeklyNewsFeed = FeedActivity.FLU_UPDATES;
			return lastWeeklyNewsFeed;
		}
		if (app.fluPodcastsFeed != null && lastWeeklyNewsFeed != null && 
				lastWeeklyNewsFeed.intValue() == FeedActivity.FLU_UPDATES) {
			lastWeeklyNewsFeed = FeedActivity.FLU_PODCASTS;
			return lastWeeklyNewsFeed;
		}
		if (app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) != null && lastWeeklyNewsFeed != null && 
				lastWeeklyNewsFeed.intValue() == FeedActivity.FLU_PODCASTS) {
			lastWeeklyNewsFeed = FeedActivity.CDC_FEATURE_PAGES;
			return lastWeeklyNewsFeed;
		}
		
		return null;
	}
	
	public synchronized void updateCdcFeedsReadyFlag() {
		ApplicationController app = (ApplicationController)getApplicationContext();
		if (app.fluPodcastsFeed != null && app.fluUpdatesFeed != null &&
				app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID) != null &&
				app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) != null) 
			cdcFeedsReady = true;
	}
	
	public void notifyFeedsReady() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast toast = Toast.makeText(FluVilleCityActivity.this, getString(R.string.feeds_ready_notification), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}
		});

		TMXObject flyer = findLandmark(MAP_LANDMARK_FLYER_1);
		if (flyer != null)
			showArrow((float)(flyer.getX() + (flyer.getWidth() / 2)), (float)(flyer.getY() + flyer.getHeight()), false);
		flyer = findLandmark(MAP_LANDMARK_FLYER_2);
		if (flyer != null)
			showArrow((float)(flyer.getX() + (flyer.getWidth() / 2)), (float)(flyer.getY() + flyer.getHeight()), false);

		cdcFeedsReadyNotificationSent = true;
	}
	
	private void presentInfectionRateStatistics() {
		gameState.stateOfPlay = GameState.STATE_OF_PLAY_PAUSED;
		mEngine.stop();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(FluVilleCityActivity.this);
				alert.setTitle("Infection statistics");
				alert.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						gameState.stateOfPlay = GameState.STATE_OF_PLAY_RUNNING;
						mEngine.start();
					}
				});
				View statisticsView = FluVilleCityActivity.this.getLayoutInflater().inflate(R.layout.infection_stats, null);
				ImageView image = (ImageView)(statisticsView.findViewById(R.id.statistics_graph));
				image.setImageBitmap(gameState.graphInfectionRate());
				alert.setView(statisticsView);
				final AlertDialog alertDialog = alert.show();
				alertDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						gameState.stateOfPlay = GameState.STATE_OF_PLAY_RUNNING;
						mEngine.start();
					}
				});
			}
		});
	}
}
