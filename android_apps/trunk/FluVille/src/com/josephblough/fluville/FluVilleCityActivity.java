package com.josephblough.fluville;

import java.util.HashMap;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
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

	private Texture mTexture;
	public TiledTextureRegion mPlayerTextureRegion;
	
	public TMXTiledMap mTMXTiledMap;
	private HashMap<String, TMXObject> mapObjects = new HashMap<String, TMXObject>();
	
	private Texture mMenuFontTexture;
	public Font mMenuFont;
	
	private Texture mMenuItemsTexture;
	public TextureRegion mImmunizationTextureRegion;
	public TextureRegion mSanitizerTextureRegion;
	//public TextureRegion mTissueTextureRegion;
	public TextureRegion mFaceMaskTextureRegion;
	public TextureRegion mCrosshairsTextureRegion;

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
		this.mTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		//this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/player.png", 0, 0, 3, 4); // 72x128
		//this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/character_boy.png", 0, 0, 3, 4); // 72x128
		this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/rpg_sprite_walk.png", 0, 0, 8, 4); // 72x128

		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		this.mMenuItemsTexture = new Texture(128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mImmunizationTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/syringe.png", 0, 0);
		this.mSanitizerTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/sanitizer.jpg", 0, 32);
		this.mFaceMaskTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/mask.png", 0, 64);
		this.mCrosshairsTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuItemsTexture, this, 
				"gfx/crosshairs.png", 0, 96);
		this.mEngine.getTextureManager().loadTexture(this.mMenuItemsTexture);

		
		this.mMenuFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mMenuFont = new Font(this.mMenuFontTexture, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD), 12, false, Color.WHITE);
		this.mMenuFont = new Font(this.mMenuFontTexture, Typeface.create(Typeface.SERIF, Typeface.NORMAL), 12, true, Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.mMenuFontTexture);
		this.mEngine.getFontManager().loadFont(this.mMenuFont);
		
		//HUD hud = new FluVilleCityHUD(this);
		//this.mBoundChaseCamera.setHUD(hud);
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
			//this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/desert.tmx");
			//this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/sewers.tmx");
			//this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/tiled.tmx");
			this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/smaller_tiled.tmx");
		} catch (final TMXLoadException tmxle) {
			Log.e(TAG, tmxle.getMessage(), tmxle);
		}

		//final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		//scene.getFirstChild().attachChild(tmxLayer);
		//scene.getFirstChild().attachChild(this.mTMXTiledMap.getTMXLayers().get(1));
		//int mapWidth = this.mTMXTiledMap.getTileColumns() * this.mTMXTiledMap.getTileWidth();
		//int mapHeight = this.mTMXTiledMap.getTileRows() * this.mTMXTiledMap.getTileHeight();
		for (TMXLayer tmxLayer : this.mTMXTiledMap.getTMXLayers()) {
			//tmxLayer.setPosition((CAMERA_WIDTH - mapWidth) / 2, (CAMERA_HEIGHT - mapHeight) / 2);
			scene.getLastChild().attachChild(tmxLayer);
			/*int rows = tmxLayer.getTileRows();
			int columns = tmxLayer.getTileColumns();
			for (int row=0; row<rows; row++) {
				for (int column=0;column<columns;column++) {
					TMXTile tile = tmxLayer.getTMXTile(row, column);
					tile.setGlobalTileID(this.mTMXTiledMap, 0);
				}
			}*/
		}

		for (TMXObjectGroup group : this.mTMXTiledMap.getTMXObjectGroups()) {
			for (TMXObject object : group.getTMXObjects()) {
				Log.d(TAG, "Object: " + object.getName() + " at (" + object.getX() + ", " + object.getY() + ")");
				mapObjects.put(object.getName(), object);
			}
		}

		/* Make the camera not exceed the bounds of the TMXEntity. */
		/*this.mBoundChaseCamera.setBounds(0, this.mTMXTiledMap.getTMXLayers().get(0).getWidth(), 
				0, this.mTMXTiledMap.getTMXLayers().get(0).getHeight());
		this.mBoundChaseCamera.setBoundsEnabled(true);*/

		/* Calculate the coordinates for the face, so its centered on the camera. */
		//final int centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
		//final int centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight()) / 2;

		/*this.mScrollDetector = new SurfaceScrollDetector(this);
		if(MultiTouch.isSupportedByAndroidVersion()) {
			try {
				this.mPinchZoomDetector = new PinchZoomDetector(this);
			} catch (final MultiTouchException e) {
				this.mPinchZoomDetector = null;
			}
		} else {
			this.mPinchZoomDetector = null;
		}*/
		
		//for (int i=0; i<5; i++)
			//addWalker(scene);
		
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
			this.mEngine.getScene().registerUpdateHandler(new TimerHandler(MathUtils.random(0.1f, 5.0f), new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					addWalker();
				}
			}));
		}
	}
/*
	@Override
	public void onScroll(ScrollDetector pScollDetector, TouchEvent pTouchEvent,
			float pDistanceX, float pDistanceY) {
		final float zoomFactor = this.mBoundChaseCamera.getZoomFactor();
		this.mBoundChaseCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
		this.mPinchZoomStartedCameraZoomFactor = this.mBoundChaseCamera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		this.mBoundChaseCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
	}

	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		this.mBoundChaseCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
	}
*/

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		/*if(this.mPinchZoomDetector != null) {
			this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

			if(this.mPinchZoomDetector.isZooming()) {
				this.mScrollDetector.setEnabled(false);
			} else {
				if(pSceneTouchEvent.isActionDown()) {
					this.mScrollDetector.setEnabled(true);
				}
				this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
			}
		} else {
			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		}*/

		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void addWalker() {
		addWalker(this.mEngine.getScene());
	}
	
	private void addWalker(final Scene scene) {
		/* Create the sprite and add it to the scene. */
		//final AnimatedSprite player = new AnimatedSprite(centerX, centerY, this.mPlayerTextureRegion);
		final TMXObject spawnPoint = getRandomOrigin();
		final FluVilleResident player = new FluVilleResident(this, scene, spawnPoint, this.mPlayerTextureRegion);
		//this.mBoundChaseCamera.setChaseEntity(player);

		//final Path path = new Path(5).to(0, 160).to(0, 500).to(600, 500).to(600, 160).to(0, 160);
		final TMXObject endPoint = getRandomDestination();
		player.setDestination(endPoint);
		
		/* Now we are going to create a rectangle that will  always highlight the tile below the feet of the pEntity. */
		/*final Rectangle currentTileRectangle = new Rectangle(0, 0, this.mTMXTiledMap.getTileWidth(), this.mTMXTiledMap.getTileHeight());
		currentTileRectangle.setColor(1, 0, 0, 0.25f);
		scene.getLastChild().attachChild(currentTileRectangle);*/

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				/* Get the scene-coordinates of the players feet. */
				//final float[] playerFootCordinates = player.convertLocalToSceneCoordinates(12, 31);

				/* Get the tile the feet of the player are currently waking on. */
				/*final TMXTile tmxTile = tmxLayer.getTMXTileAt(playerFootCordinates[Constants.VERTEX_INDEX_X], playerFootCordinates[Constants.VERTEX_INDEX_Y]);
				if(tmxTile != null) {
					// tmxTile.setTextureRegion(null); <-- Rubber-style removing of tiles =D
					currentTileRectangle.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
				}*/
			}
		});
		scene.getLastChild().attachChild(player);
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