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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

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
	
	public static final float SECONDS_PER_FLUVILLE_HOUR = 5.0f;
	
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
	//public TextureRegion mTissueTextureRegion;
	public TextureRegion mSpongeTextureRegion;
	public TextureRegion mSendHomeTextureRegion;

	public GameState gameState;
	
	public static RectanglePool RECTANGLE_POOL = new RectanglePool();
	
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
		this.mDialogFont = new Font(this.mDialogFontTexture, Typeface.create(Typeface.SERIF, Typeface.NORMAL), 20, true, Color.BLACK);
		
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
			//this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/even_smaller_tiled.tmx");
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
		retrievePreviouslyShownMessages();		
		
		HUD hud = new FluVilleCityHUD(this);
		this.mBoundChaseCamera.setHUD(hud);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
		for (int i=0; i<5; i++) {
			addResident();
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

		//if (!gameState.shownWelcomeMessage) {
			displayMessage(getString(R.string.welcome));
			gameState.shownWelcomeMessage = true;
			updatePreviouslyShownMessages("shownWelcomeMessage");
		//}
	}
	
	private void incrementHour() {
		gameState.hourOfDay++;

		for (FluVilleResident resident : gameState.residents) {
			// Decrease any accumulations that we're keeping track of for the resident
			if (resident.hoursOfSanitizerRemaining > 0) {
				resident.reduceSanitizerProtect();
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
			
			// Reset sanitizer at the beginning of each day
			if (resident.hoursOfSanitizerRemaining > 0) {
				resident.hoursOfSanitizerRemaining = 0;
			}
		}
		
		// Increase flu shot vaccines, hand sanitizer doses available when appropriate
		if (gameState.day % GameState.DAYS_BETWEEN_FLU_SHOT_REFILLS == 0) {
			gameState.immunizationsRemaining += GameState.FLU_SHOT_REFILL_SIZE;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateFluShotsLabel();
		}
		if (gameState.day % GameState.DAYS_BETWEEN_HAND_SANITIZER_REFILLS == 0) {
			gameState.handSanitizerDosesRemaining += GameState.HAND_SANITIZER_REFILL_SIZE;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateHandSanitizersLabel();
		}

		// Add more residents
		for (int i=0; i<5; i++) {
			addResident();
		}
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
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
					case FluVilleCityHUD.HUD_MENU_GRAB_RESIDENTS:
						sendResidentHome(resident);
						break;
					}
					return true;
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
				// Iterate through the landmarks and clean if we're moving over them
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
		if (!resident.infected && resident.hoursOfSanitizerRemaining <= 0 && 
				gameState.handSanitizerDosesRemaining > 0) {
			resident.applyHandSanitizer();
			gameState.handSanitizerDosesRemaining--;
			((FluVilleCityHUD)mBoundChaseCamera.getHUD()).updateHandSanitizersLabel();
		}
	}
	
	public void sendResidentHome(final FluVilleResident resident) {
		Log.d(TAG, "Sending a resident home sick");
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
		switch (MathUtils.random(0, 5)) {
		case 0:
			return mapObjects.get(MAP_LANDMARK_PIZZA);
		case 1:
			return mapObjects.get(MAP_LANDMARK_OFFICE_1);
		case 2:
			return mapObjects.get(MAP_LANDMARK_OFFICE_2);
		case 3:
			return mapObjects.get(MAP_LANDMARK_OFFICE_3);
		//case 4:
			//return mapObjects.get(MAP_LANDMARK_OFFICE_4);
		case 4:
			return mapObjects.get(MAP_LANDMARK_STORE_1);
		case 5:
			return mapObjects.get(MAP_LANDMARK_STORE_2);
		}
		return null;
	}

	public int getMapWidth() {
		return this.mTMXTiledMap.getTileColumns() * this.mTMXTiledMap.getTileWidth();
	}
	
	private void displayMessage(final String... messages) {
		final Scene messageScene = new Scene(1);
		//messageScene.setBackground(new ColorBackground(1.0f, 1.0f, 1.0f, 1.0f));
		messageScene.setBackgroundEnabled(false);
		final Sprite bubble = new Sprite(0, 0, mSpeechBubble);
		final Text text = new Text(0.0f, 0.0f, FluVilleCityActivity.this.mDialogFont, messages[0]);
		//bubble.setHeight(CAMERA_HEIGHT * 2.0f / 3.0f/* 400.0f*/);
		//bubble.setWidth(CAMERA_WIDTH * 2.0f / 3.0f/* 100.0f*/);
		bubble.setWidth(text.getWidth() + 25.0f);
		bubble.setHeight(text.getHeight() + 125.0f);
		// Center the text
		text.setPosition((bubble.getWidth() - text.getWidth()) / 2, 25.0f/*(bubble.getHeight() - text.getHeight()) / 2*/);
		bubble.attachChild(text);
		// Bottom left corner
		bubble.setPosition(0.0f, CAMERA_HEIGHT - bubble.getHeight());
		messageScene.attachChild(bubble);
		//messageScene.attachChild(new Text(10.0f, 80.f, FluVilleCityActivity.this.mDialogFont, message));
		mEngine.getScene().setChildScene(messageScene, false, true, true);

		// Keep the dialog up for at least a second before registering a removal handler
		this.mEngine.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {
			
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
					new MoveYModifier(0.5f, pY2, pY)//,
					//new FadeOutModifier(3.0f)
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
	
	public void retrievePreviouslyShownMessages() {
		SharedPreferences preferences = this.getSharedPreferences(getClass().getName(), 0);
		gameState.shownWelcomeMessage = preferences.getBoolean("shownWelcomeMessage", false);
		//gameState.shownImmunizationMessage = preferences.getBoolean("shownImmunizationMessage", false);
		//gameState.shownSanitizerMessage = preferences.getBoolean("shownSanitizerMessage", false);
		//gameState.shownSpongeMessage = preferences.getBoolean("shownSpongeMessage", false);
		//gameState.shownSendHomeMessage = preferences.getBoolean("shownSendHomeMessage", false);
		//gameState.shownInfectedPersonMessage = preferences.getBoolean("shownInfectedPersonMessage", false);
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
	}
	
	public void displayImmunizationBenefits() {
		Log.d(TAG, "displayImmunizationBenefits");
		displayMessage(getString(R.string.immunization_benefits), getString(R.string.immunization_instructions));
		gameState.shownImmunizationMessage = true;
		updatePreviouslyShownMessages("shownImmunizationMessage");
	}

	public void displayHandSanitizerBenefits() {
		Log.d(TAG, "displayHandSanitizerBenefits");
		displayMessage(getString(R.string.sanitizer_benefits), getString(R.string.sanitizer_instructions));
		gameState.shownSanitizerMessage = true;
		updatePreviouslyShownMessages("shownSanitizerMessage");
	}

	public void displaySpongeUsage() {
		Log.d(TAG, "displaySpongeUsage");
		displayMessage(getString(R.string.sponge_usage), getString(R.string.sponge_instructions));
		gameState.shownSpongeMessage = true;
		updatePreviouslyShownMessages("shownSpongeMessage");
	}
	
	public void displaySendResidentHomeUsage() {
		Log.d(TAG, "displaySendResidentHomeUsage");
		displayMessage(getString(R.string.send_residents_home_reason), getString(R.string.send_residents_home_instructions));
		gameState.shownSendHomeMessage = true;
		updatePreviouslyShownMessages("shownSendHomeMessage");
	}
	
	public void displayInfectedPersonWarning() {
		Log.d(TAG, "displayInfectedPersonWarning");
		displayMessage(getString(R.string.infected_person_warning), getString(R.string.infected_person_instructions));
		gameState.shownInfectedPersonMessage = true;
		updatePreviouslyShownMessages("shownInfectedPersonMessage");
	}
	
	public void displayNews() {
		TMXObject flyer = findLandmark(MAP_LANDMARK_FLYER_1);
		if (flyer != null)
			showArrow((float)(flyer.getX() + (flyer.getWidth() / 2)), (float)(flyer.getY() + flyer.getHeight()), false);

		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(FluVilleCityActivity.this);
				alert.setMessage("News feeds from the CDC");
				alert.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
				alert.show();
			}
		});
	}
}