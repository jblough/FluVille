package com.josephblough.fluville;

import java.util.HashMap;

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
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.MathUtils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

public class FluVilleCityActivity extends BaseGameActivity implements IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "FluVilleCityActivity";
	
	//public static final int CAMERA_WIDTH = 1000;
	//public static final int CAMERA_HEIGHT = 700;
	public static final int CAMERA_WIDTH = 760;
	public static final int CAMERA_HEIGHT = 480;
	//public static final int CAMERA_WIDTH = 480;
	//public static final int CAMERA_HEIGHT = 320;
	
	public static final float SECONDS_PER_FLUVILLE_HOUR = 5.0f;
	
	public static final String MAP_LANDMARK_LEFT_RESIDENTIAL_INTERSECTION = "TopLeftResidentialCorner";
	public static final String MAP_LANDMARK_RIGHT_RESIDENTIAL_INTERSECTION = "TopRightResidentialCorner";
	public static final String MAP_LANDMARK_RESIDENTIAL_SPAWN_POINT_1 = "SpawnPoint1";
	public static final String MAP_LANDMARK_RESIDENTIAL_SPAWN_POINT_2 = "SpawnPoint2";
	public static final String MAP_LANDMARK_FLYER_1 = "Flyer1";
	public static final String MAP_LANDMARK_FLYER_2 = "Flyer2";
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
	
	public TMXTiledMap mTMXTiledMap;
	private HashMap<String, TMXObject> mapObjects = new HashMap<String, TMXObject>();
	
	private Texture mMenuFontTexture;
	public Font mMenuFont;
	
	private Texture mMenuItemsTexture;
	public TextureRegion mImmunizationTextureRegion;
	public TextureRegion mSanitizerTextureRegion;
	//public TextureRegion mTissueTextureRegion;
	public TextureRegion mFaceMaskTextureRegion;
	public TextureRegion mSendHomeTextureRegion;

	public GameState gameState;
	
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
		//this.mBoundChaseCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mBoundChaseCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera));
	}

	@Override
	public void onLoadResources() {
		this.mPlayerTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mPlayerTexture, this, "gfx/rpg_sprite_walk.png", 0, 0, 8, 4); // 72x128

		this.mInfectedPlayerTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mInfectedPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mInfectedPlayerTexture, this, "gfx/infected_sprite_walk.png", 0, 0, 8, 4); // 72x128

		this.mImmunizedPlayerTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mImmunizedPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mImmunizedPlayerTexture, this, "gfx/immunized_sprite_walk.png", 0, 0, 8, 4); // 72x128
		
		this.mEngine.getTextureManager().loadTextures(this.mPlayerTexture, this.mInfectedPlayerTexture, this.mImmunizedPlayerTexture);
		
		
		this.mMenuItemsTexture = new Texture(128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mImmunizationTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/syringe.png", 0, 0);
		this.mSanitizerTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/sanitizer.jpg", 0, 32);
		this.mFaceMaskTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/mask.png", 0, 64);
		this.mSendHomeTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/ic_launcher_home.png", 0, 96);
		this.mEngine.getTextureManager().loadTexture(this.mMenuItemsTexture);

		
		this.mMenuFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mMenuFont = new Font(this.mMenuFontTexture, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD), 12, false, Color.WHITE);
		this.mMenuFont = new Font(this.mMenuFontTexture, Typeface.create(Typeface.SERIF, Typeface.NORMAL), 12, true, Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.mMenuFontTexture);
		this.mEngine.getFontManager().loadFont(this.mMenuFont);
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(2);
		//scene.setOnAreaTouchTraversalFrontToBack();

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
				Log.d(TAG, "Object: " + object.getName() + " at (" + object.getX() + ", " + object.getY() + ")");
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
		for (int i=0; i<5; i++) {
			addWalker();
		}

		this.mEngine.registerUpdateHandler(new TimerHandler(SECONDS_PER_FLUVILLE_HOUR, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (gameState.hourOfDay < 23) {
					incrementHour();
				}
				else {
					incrementDay();
				}
				
				((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateClock();
			}
		}));
	}
	
	private void incrementHour() {
		gameState.hourOfDay++;

		for (FluVilleResident resident : gameState.residents) {
			// Decrease any accumulations that we're keeping track of for the resident
			if (resident.hoursOfSanitizerRemaining > 0) {
				resident.hoursOfSanitizerRemaining--;
			}

			// Get the residents walking again
			if (!resident.isWalking && !resident.isAtWork()) {
				//Log.d(TAG, "Sending resident to work from home");
				mEngine.getScene().getLastChild().attachChild(resident);
				resident.walk(resident.placeOfWork);
			}
			else if (!resident.isWalking && resident.isAtWork()) {
				// Go home
				//Log.d(TAG, "Sending resident home from work");
				mEngine.getScene().getLastChild().attachChild(resident);
				resident.walk(resident.home);
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
			
			// Sanitizer and face masks reset at the beginning of
			//	each day
			if (resident.hoursOfSanitizerRemaining > 0) {
				resident.hoursOfSanitizerRemaining = 0;
			}
			if (resident.hasFaceMask) {
				resident.hasFaceMask = false;
			}
		}
		
		// Increase flu shot vaccines, hand sanitizer doses, and face masks
		//	available when appropriate
		if (gameState.day % GameState.DAYS_BETWEEN_FLU_SHOT_REFILLS == 0) {
			gameState.immunizationsRemaining += GameState.FLU_SHOT_REFILL_SIZE;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateFluShotsLabel();
		}
		if (gameState.day % GameState.DAYS_BETWEEN_HAND_SANITIZER_REFILLS == 0) {
			gameState.handSanitizerDosesRemaining += GameState.HAND_SANITIZER_REFILL_SIZE;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateHandSanitizersLabel();
		}
		if (gameState.day % GameState.DAYS_BETWEEN_FACE_MASK_REFILLS == 0) {
			gameState.faceMasksRemaining += GameState.FACE_MASK_REFILL_SIZE;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateFaceMasksLabel();
		}

		// Add more residents
		for (int i=0; i<5; i++) {
			addWalker();
		}
		
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		for (FluVilleResident resident : gameState.residents) {
			if (resident.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
				Log.d(TAG, "Tapped on a resident");
				switch (((FluVilleCityHUD)mBoundChaseCamera.getHUD()).currentMenuSelection) {
				case FluVilleCityHUD.HUD_MENU_IMMUNIZATION:
					immunizeResident(resident);
					break;
				case FluVilleCityHUD.HUD_MENU_SANITIZER:
					sanitizeResident(resident);
					break;
				case FluVilleCityHUD.HUD_MENU_FACE_MASKS:
					giveFacemaskToResident(resident);
					break;
				case FluVilleCityHUD.HUD_MENU_GRAB_RESIDENTS:
					sendResidentHome(resident);
					break;
				}
				return true;
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
		}
	}
	
	public void sanitizeResident(final FluVilleResident resident) {
		if (!resident.infected && gameState.handSanitizerDosesRemaining > 0) {
			resident.applyHandSanitizer();
			gameState.handSanitizerDosesRemaining--;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateHandSanitizersLabel();
		}
	}
	
	public void giveFacemaskToResident(final FluVilleResident resident) {
		if (!resident.hasFaceMask && !resident.infected && gameState.faceMasksRemaining > 0) {
			resident.giveFaceMask();
			gameState.faceMasksRemaining--;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateFaceMasksLabel();
		}
	}
	
	public void sendResidentHome(final FluVilleResident resident) {
		Log.d(TAG, "Sending a resident home sick");
		resident.sendHome();
	}
	
	public void addWalker() {
		addWalker(this.mEngine.getScene());
	}
	
	private void addWalker(final Scene scene) {
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
		switch (MathUtils.random(0, 4)) {
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
		}
		return null;
	}

	public int getMapWidth() {
		return this.mTMXTiledMap.getTileColumns() * this.mTMXTiledMap.getTileWidth();
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}