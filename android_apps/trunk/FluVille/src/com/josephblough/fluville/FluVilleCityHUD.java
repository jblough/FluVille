package com.josephblough.fluville;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.util.MathUtils;

import android.util.Log;

public class FluVilleCityHUD extends HUD implements IOnAreaTouchListener {

	private final static String TAG = "FluVilleCityHUD";
	
	public static final int HUD_MENU_NONE = 0;
	public static final int HUD_MENU_IMMUNIZATION = 1;
	public static final int HUD_MENU_GRAB_RESIDENTS = 2;
	public static final int HUD_MENU_SANITIZER = 3;
	
	private FluVilleCityActivity activity;
	private boolean safeToReleaseMoreWalkers = true;
	private Sprite fluShotMenuItem;
	private Sprite sanitizerMenuItem;
	private Sprite dragResidentMenuItem;
	public int currentMenuSelection = HUD_MENU_NONE;
	
	public FluVilleCityHUD(final FluVilleCityActivity activity) {
		super();
		this.safeToReleaseMoreWalkers = true;
		this.activity = activity;
		float x = (float)this.activity.getMapWidth() + (float)((FluVilleCityActivity.CAMERA_WIDTH - this.activity.getMapWidth()) / 2);
		float y = 20.f;
		float gap = 60.0f;

		fluShotMenuItem = new Sprite(x, y, this.activity.mImmunizationTextureRegion.clone());
		this.registerTouchArea(fluShotMenuItem);
		this.getLastChild().attachChild(fluShotMenuItem);
		y += gap;

		sanitizerMenuItem = new Sprite(x, y, this.activity.mSanitizerTextureRegion.clone());
		this.registerTouchArea(sanitizerMenuItem);
		this.getLastChild().attachChild(sanitizerMenuItem);
		y += gap;

		dragResidentMenuItem = new Sprite(x, y, this.activity.mImmunizationTextureRegion.clone());
		this.registerTouchArea(dragResidentMenuItem);
		this.getLastChild().attachChild(dragResidentMenuItem);
		y += gap;
		
		this.setOnAreaTouchListener(this);
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pTouchArea.equals(fluShotMenuItem)) {
			Log.d(TAG, "Hit menu item");
			releaseWalkers();
			currentMenuSelection = HUD_MENU_IMMUNIZATION;
		}
		else if (pTouchArea.equals(dragResidentMenuItem)) {
			currentMenuSelection = HUD_MENU_GRAB_RESIDENTS;
		}
		else if (pTouchArea.equals(sanitizerMenuItem)) {
			currentMenuSelection = HUD_MENU_SANITIZER;
		}
		else {
			Log.d(TAG, "Something else");
			currentMenuSelection = HUD_MENU_NONE;
		}
		updateMenuHilight();
		
		return false;
	}
	
	private void releaseWalkers() {
		if (safeToReleaseMoreWalkers) {
			Log.d(TAG, "Sending out resident");
			safeToReleaseMoreWalkers = false;
			for (int i=0; i<5; i++) {
				registerUpdateHandler(new TimerHandler(MathUtils.random(0.1f, 5.0f), new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						activity.addWalker();
					}
				}));
			}

			registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					safeToReleaseMoreWalkers = true;
				}
			}));
		}
	}
	
	private void updateMenuHilight() {
		switch (currentMenuSelection) {
		case HUD_MENU_IMMUNIZATION:
			break;
		case HUD_MENU_GRAB_RESIDENTS:
			break;
		case HUD_MENU_SANITIZER:
			break;
		case HUD_MENU_NONE:
			// Unhilight all of the menu items
			break;
		}
	}
}
