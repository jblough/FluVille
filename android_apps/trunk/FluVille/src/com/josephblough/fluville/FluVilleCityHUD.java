package com.josephblough.fluville;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.util.MathUtils;

import android.util.Log;

public class FluVilleCityHUD extends HUD implements IOnAreaTouchListener {

	private final static String TAG = "FluVilleCityHUD";
	
	public static final int HUD_MENU_NONE = 0;
	public static final int HUD_MENU_IMMUNIZATION = 1;
	public static final int HUD_MENU_GRAB_RESIDENTS = 2;
	public static final int HUD_MENU_SANITIZER = 3;
	public static final int HUD_MENU_SPONGE = 4;
	
	private FluVilleCityActivity activity;
	private boolean safeToReleaseMoreWalkers = true;
	private ChangeableText dayLabel;
	private ChangeableText hourOfDayLabel;
	private Rectangle fluShotMenuItem;
	private Rectangle sanitizerMenuItem;
	private Rectangle dragResidentMenuItem;
	private Rectangle spongeMenuItem;
	private ChangeableText fluShotsRemainingLabel;
	private ChangeableText handSanitizerRemainingLabel;
	public int currentMenuSelection = HUD_MENU_NONE;
	
	public FluVilleCityHUD(final FluVilleCityActivity activity) {
		super();
		this.safeToReleaseMoreWalkers = true;
		this.activity = activity;
		float x = (float)this.activity.getMapWidth() + (float)((FluVilleCityActivity.CAMERA_WIDTH - this.activity.getMapWidth()) / 3);
		float y = 20.f;
		float gap = 60.0f;

		this.dayLabel = new ChangeableText(x, y, activity.mMenuFont, "Day: 9999");
		this.dayLabel.setText("Day: " + this.activity.gameState.day);
		this.getLastChild().attachChild(this.dayLabel);
		y += dayLabel.getHeight();

		this.hourOfDayLabel = new ChangeableText(x, y, activity.mMenuFont, "Hour: 99");
		this.hourOfDayLabel.setText("Hour: " + this.activity.gameState.hourOfDay);
		this.getLastChild().attachChild(this.hourOfDayLabel);
		y += gap;

		// Immunization menu item
		fluShotMenuItem = new Rectangle(this.activity.getMapWidth(), y, 
				(FluVilleCityActivity.CAMERA_WIDTH - this.activity.getMapWidth()), gap);
		fluShotMenuItem.setColor(0, 0, 0);
		this.getLastChild().attachChild(fluShotMenuItem);
		this.registerTouchArea(fluShotMenuItem);
		y += gap;

		final Sprite fluShotMenuImage = new Sprite(0, 0, this.activity.mImmunizationTextureRegion);
		fluShotMenuImage.setPosition(fluShotMenuItem.getWidth() / 2 - fluShotMenuImage.getWidth() / 2, 
				fluShotMenuItem.getHeight() / 3 - fluShotMenuImage.getHeight() / 2);
		fluShotMenuItem.attachChild(fluShotMenuImage);
		
		fluShotsRemainingLabel = new ChangeableText(0, 0, activity.mMenuFont, "99 remaining");
		fluShotsRemainingLabel.setText("" + activity.gameState.immunizationsRemaining + " remaining");
		fluShotsRemainingLabel.setPosition(fluShotMenuItem.getWidth() / 2 - fluShotsRemainingLabel.getWidth() / 2, 
				fluShotMenuImage.getY() + fluShotMenuImage.getHeight() + 2);
		fluShotMenuItem.attachChild(fluShotsRemainingLabel);

		// Sanitizer menu item
		sanitizerMenuItem = new Rectangle(this.activity.getMapWidth(), y, 
				(FluVilleCityActivity.CAMERA_WIDTH - this.activity.getMapWidth()), gap);
		sanitizerMenuItem.setColor(0, 0, 0);
		this.getLastChild().attachChild(sanitizerMenuItem);
		this.registerTouchArea(sanitizerMenuItem);
		y += gap + 5;
		
		final Sprite sanitizerMenuImage = new Sprite(0, 0, this.activity.mSanitizerTextureRegion);
		sanitizerMenuImage.setPosition(sanitizerMenuItem.getWidth() / 2 - sanitizerMenuImage.getWidth() / 2, 
				sanitizerMenuItem.getHeight() / 3 - sanitizerMenuImage.getHeight() / 2);
		sanitizerMenuItem.attachChild(sanitizerMenuImage);

		handSanitizerRemainingLabel = new ChangeableText(0, 0, activity.mMenuFont, "99 remaining");
		handSanitizerRemainingLabel.setText("" + activity.gameState.handSanitizerDosesRemaining + " remaining");
		handSanitizerRemainingLabel.setPosition(sanitizerMenuItem.getWidth() / 2 - handSanitizerRemainingLabel.getWidth() / 2, 
				sanitizerMenuImage.getY() + sanitizerMenuImage.getHeight() + 2);
		sanitizerMenuItem.attachChild(handSanitizerRemainingLabel);
		
		// Sponge menu item
		spongeMenuItem = new Rectangle(this.activity.getMapWidth(), y, 
				(FluVilleCityActivity.CAMERA_WIDTH - this.activity.getMapWidth()), gap);
		spongeMenuItem.setColor(0, 0, 0);
		this.getLastChild().attachChild(spongeMenuItem);
		this.registerTouchArea(spongeMenuItem);
		y += gap + 5;

		final Sprite spongeMenuItemMenuImage = new Sprite(0, 0, this.activity.mSpongeTextureRegion);
		spongeMenuItemMenuImage.setPosition(spongeMenuItem.getWidth() / 2 - spongeMenuItemMenuImage.getWidth() / 2, 
				spongeMenuItem.getHeight() / 2 - spongeMenuItemMenuImage.getHeight() / 2);
		spongeMenuItem.attachChild(spongeMenuItemMenuImage);
		
		// Drag residents menu item
		dragResidentMenuItem = new Rectangle(this.activity.getMapWidth(), y, 
				(FluVilleCityActivity.CAMERA_WIDTH - this.activity.getMapWidth()), gap);
		dragResidentMenuItem.setColor(0, 0, 0);
		this.getLastChild().attachChild(dragResidentMenuItem);
		this.registerTouchArea(dragResidentMenuItem);
		y += gap + 5;

		final Sprite dragResidentMenuImage = new Sprite(0, 0, this.activity.mSendHomeTextureRegion);
		dragResidentMenuImage.setPosition(dragResidentMenuItem.getWidth() / 2 - dragResidentMenuImage.getWidth() / 2, 
				dragResidentMenuItem.getHeight() / 2 - dragResidentMenuImage.getHeight() / 2);
		dragResidentMenuItem.attachChild(dragResidentMenuImage);

		
		this.setOnAreaTouchListener(this);
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pTouchArea.equals(fluShotMenuItem)) {
			Log.d(TAG, "immunization");
			//releaseWalkers();
			if (currentMenuSelection != HUD_MENU_IMMUNIZATION) {
				currentMenuSelection = HUD_MENU_IMMUNIZATION;
				// Display a popup explaining the benefits of immunization
				if (!activity.gameState.shownImmunizationMessage) {
					activity.displayImmunizationBenefits();
				}
			}
		}
		else if (pTouchArea.equals(sanitizerMenuItem)) {
			Log.d(TAG, "sanitizer");
			if (currentMenuSelection != HUD_MENU_SANITIZER) {
				currentMenuSelection = HUD_MENU_SANITIZER;
				// Display a popup explaining the benefits of hand sanitizer when soap and water is not available
				if (!activity.gameState.shownSanitizerMessage) {
					activity.displayHandSanitizerBenefits();
				}
			}
		}
		else if (pTouchArea.equals(spongeMenuItem)) {
			Log.d(TAG, "sponge");
			if (currentMenuSelection != HUD_MENU_SPONGE) {
				currentMenuSelection = HUD_MENU_SPONGE;
				// Display a popup explaining the benefits of face masks to prevent infection
				if (!activity.gameState.shownSpongeMessage) {
					activity.displaySpongeUsage();
				}
			}
		}
		else if (pTouchArea.equals(dragResidentMenuItem)) {
			Log.d(TAG, "drag residents");
			if (currentMenuSelection != HUD_MENU_GRAB_RESIDENTS) {
				currentMenuSelection = HUD_MENU_GRAB_RESIDENTS;
				// Display a popup explaining the need to stay inside when sick
				if (!activity.gameState.shownSendHomeMessage) {
					activity.displaySendResidentHomeUsage();
				}
			}
		}
		else {
			Log.d(TAG, "Something else");
			currentMenuSelection = HUD_MENU_NONE;
		}
		updateMenuHilight();
		
		return false;
	}
	
	public void releaseWalkers() {
		if (safeToReleaseMoreWalkers) {
			Log.d(TAG, "Sending out resident");
			safeToReleaseMoreWalkers = false;
			for (int i=0; i<5; i++) {
				registerUpdateHandler(new TimerHandler(MathUtils.random(0.1f, 5.0f), new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						activity.addResident();
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
			this.fluShotMenuItem.setColor(0.3f, 0.3f, 0.3f);
			this.sanitizerMenuItem.setColor(0, 0, 0);
			this.dragResidentMenuItem.setColor(0, 0, 0);
			this.spongeMenuItem.setColor(0, 0, 0);
			break;
		case HUD_MENU_GRAB_RESIDENTS:
			this.fluShotMenuItem.setColor(0, 0, 0);
			this.sanitizerMenuItem.setColor(0, 0, 0);
			this.dragResidentMenuItem.setColor(0.3f, 0.3f, 0.3f);
			this.spongeMenuItem.setColor(0, 0, 0);
			break;
		case HUD_MENU_SANITIZER:
			this.fluShotMenuItem.setColor(0, 0, 0);
			this.sanitizerMenuItem.setColor(0.3f, 0.3f, 0.3f);
			this.dragResidentMenuItem.setColor(0, 0, 0);
			this.spongeMenuItem.setColor(0, 0, 0);
			break;
		case HUD_MENU_SPONGE:
			this.fluShotMenuItem.setColor(0, 0, 0);
			this.sanitizerMenuItem.setColor(0, 0, 0);
			this.dragResidentMenuItem.setColor(0, 0, 0);
			this.spongeMenuItem.setColor(0.3f, 0.3f, 0.3f);
			break;
		case HUD_MENU_NONE:
			// Unhilight all of the menu items
			this.fluShotMenuItem.setColor(0, 0, 0);
			this.sanitizerMenuItem.setColor(0, 0, 0);
			this.dragResidentMenuItem.setColor(0, 0, 0);
			this.spongeMenuItem.setColor(0, 0, 0);
			break;
		}
	}
	
	public void updateClock() {
		this.dayLabel.setText("Day: " + this.activity.gameState.day);
		this.hourOfDayLabel.setText("Hour: " + this.activity.gameState.hourOfDay);
	}
	
	public void updateFluShotsLabel() {
		this.fluShotsRemainingLabel.setText("" + activity.gameState.immunizationsRemaining + " remaining");
	}
	
	public void updateHandSanitizersLabel() {
		this.handSanitizerRemainingLabel.setText("" + activity.gameState.handSanitizerDosesRemaining + " remaining");
	}
}
