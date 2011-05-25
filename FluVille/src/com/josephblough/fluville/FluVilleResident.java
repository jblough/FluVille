package com.josephblough.fluville;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierMatcher;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.MathUtils;
import org.anddev.andengine.util.modifier.IModifier;

import android.util.Log;

public class FluVilleResident extends AnimatedSprite {

	private static final String TAG = "FluVilleResident";

	private static final int HOURS_OF_PROTECTION_FROM_HAND_SANITIZER = 24;
	private static final int MIN_DAYS_INFECTED = 5;
	private static final int MAX_DAYS_INFECTED = 7;
	
	public static final int CYCLES_BEFORE_INFECTION = 10;
	
	private FluVilleCityActivity activity;
	private Scene scene;
	
	public TMXObject home;
	public TMXObject placeOfWork;
	public TMXObject currentDestination;
	
	// state variables
	public boolean infected;
	public boolean immunized;
	public int hoursOfSanitizerRemaining;
	public int daysOfInfectionRemaining;
	public int cyclesAroundInfectedPerson;
	//public PathModifier pathOfTravel;
	public boolean isWalking;
	public boolean wasSentHomeSick;
	public ChangeableText protectionLabel;
	
	private MainPathListener mainPathListener = new MainPathListener();
	private HomePathListener homePathListener = new HomePathListener();
	
	private static final long[] animationDurations = new long[]{200, 200, 200, 200, 200, 200, 200, 200};
	
	public FluVilleResident(final FluVilleCityActivity activity, final Scene scene, final TMXObject origin, final TiledTextureRegion texture) {
		super(origin.getX() + origin.getWidth() / 2 - texture.getTileWidth() / 2,
				origin.getY() + origin.getHeight() / 2 - texture.getTileHeight() / 2, texture);
		
		Log.d(TAG, "Creating FluVilleResident");
		this.activity = activity;
		this.scene = scene;
		
		this.home = origin;
		this.infected = false;
		this.immunized = false;
		this.hoursOfSanitizerRemaining = 0;
		this.daysOfInfectionRemaining = 0;
		this.cyclesAroundInfectedPerson = 0;
		this.isWalking = false;
		this.wasSentHomeSick = false;
		
		this.protectionLabel = new ChangeableText(-10.0f, 0, this.activity.mMenuFont, "10");
	}

	public void walk() {
		//Log.d(TAG, "walk");
		walk(this.placeOfWork);
	}
	
	public void walk(final TMXObject destination) {
		//Log.d(TAG, "walk to destination");
		this.isWalking = true;
		this.currentDestination = destination;
		
		final Path path = calculatePath(getX(), getY(), 
				destination.getX() + MathUtils.random(0, destination.getWidth()), destination.getY());

		PathModifier pathOfTravel = new PathModifier(getRandomSpeed(), path, null, mainPathListener);
		this.registerEntityModifier(pathOfTravel);
	}

	private Path calculatePath(final float fromX, final float fromY, final float toX, final float toY) {
		boolean goingDown = fromY < toY;
		boolean leftPath = (goingDown) ? (fromX < FluVilleCityActivity.CAMERA_WIDTH / 2) : (toX < FluVilleCityActivity.CAMERA_WIDTH / 2);
		boolean stopAtResidential = false;
		boolean stopAtTop = false;
		boolean stopAtMiddle = false;
		boolean stopAtBottom = false;
		int pathPoints = 2;
		Path path = null;
		final int leeway = 10;

		TMXObject residentialObject = null;
		TMXObject topObject = null;
		TMXObject middleObject = null;
		TMXObject bottomObject = null;

		// Find the points on the map
		if (leftPath) {
			// Check the points in the following order:
			//MAP_LANDMARK_BOTTOM_LEFT_INTERSECTION
			bottomObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_BOTTOM_LEFT_INTERSECTION);
			//MAP_LANDMARK_MIDDLE_LEFT_INTERSECTION
			middleObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_MIDDLE_LEFT_INTERSECTION);
			//MAP_LANDMARK_TOP_LEFT_INTERSECTION
			topObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_TOP_LEFT_INTERSECTION);
			//MAP_LANDMARK_LEFT_RESIDENTIAL_INTERSECTION
			residentialObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_LEFT_RESIDENTIAL_INTERSECTION);
		}
		else {
			// Check the points in the following order:
			//MAP_LANDMARK_BOTTOM_RIGHT_INTERSECTION
			bottomObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_BOTTOM_RIGHT_INTERSECTION);
			//MAP_LANDMARK_MIDDLE_RIGHT_INTERSECTION
			middleObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_MIDDLE_RIGHT_INTERSECTION);
			//MAP_LANDMARK_TOP_RIGHT_INTERSECTION
			topObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_TOP_RIGHT_INTERSECTION);
			//MAP_LANDMARK_RIGHT_RESIDENTIAL_INTERSECTION
			residentialObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_RIGHT_RESIDENTIAL_INTERSECTION);
		}
		
		if (goingDown) {
			// Determine if we need to visit the points
			//Log.d(TAG, "Residential comparing " + fromY + " to " + (residentialObject.getY() + residentialObject.getHeight()));
			if (fromY < (residentialObject.getY() + residentialObject.getHeight()) &&
					toY + leeway > residentialObject.getY()) {
				pathPoints++;
				stopAtResidential = true;
			}
			//Log.d(TAG, "Top comparing " + fromY + " to " + (topObject.getY() + topObject.getHeight()));
			if (fromY < (topObject.getY() + topObject.getHeight()) &&
					toY + leeway > topObject.getY()) {
				pathPoints++;
				stopAtTop = true;
			}
			//Log.d(TAG, "Middle comparing " + fromY + " to " + (middleObject.getY() + middleObject.getHeight()));
			//Log.d(TAG, "   and " + toY + " > " + middleObject.getY());
			if (fromY < (middleObject.getY() + middleObject.getHeight()) &&
					toY + leeway > middleObject.getY()) {
				pathPoints++;
				stopAtMiddle = true;
			}
			//Log.d(TAG, "Bottom comparing " + fromY + " to " + (bottomObject.getY() + bottomObject.getHeight()));
			//Log.d(TAG, "   and " + toY + " > " + bottomObject.getY());
			if (fromY < (bottomObject.getY() + bottomObject.getHeight()) &&
					toY + leeway > bottomObject.getY()) {
				pathPoints++;
				stopAtBottom = true;
			}

			// Now add the path points in the return value
			path = new Path(pathPoints);
			path.to(fromX - getWidth() / 2, fromY - getHeight() / 2);
			if (stopAtResidential)
				addLandmarkToPath(path, residentialObject);
			if (stopAtTop)
				addLandmarkToPath(path, topObject);
			if (stopAtMiddle)
				addLandmarkToPath(path, middleObject);
			if (stopAtBottom)
				addLandmarkToPath(path, bottomObject);
			path.to(toX - getWidth() / 2, toY - getHeight() / 2);
		}
		else {
			// Determine if we need to visit the points
			//Log.d(TAG, "Bottom comparing " + fromY + " to " + (bottomObject.getY() + bottomObject.getHeight()));
			//Log.d(TAG, "   and " + toY + " > " + bottomObject.getY());
			if (fromY + getHeight() + leeway > (bottomObject.getY()) &&
					toY < bottomObject.getY() + bottomObject.getHeight()) {
				pathPoints++;
				stopAtBottom = true;
			}
			//Log.d(TAG, "Middle comparing " + fromY + " to " + (middleObject.getY() + middleObject.getHeight()));
			//Log.d(TAG, "   and " + toY + " > " + middleObject.getY());
			if (fromY + getHeight() +leeway > (middleObject.getY()) &&
					toY < middleObject.getY() + middleObject.getHeight()) {
				pathPoints++;
				stopAtMiddle = true;
			}
			//Log.d(TAG, "Top comparing " + fromY + " to " + (topObject.getY() + topObject.getHeight()));
			if (fromY + getHeight() + leeway > (topObject.getY()) &&
					toY < topObject.getY() + topObject.getHeight()) {
				pathPoints++;
				stopAtTop = true;
			}
			//Log.d(TAG, "Residential comparing " + fromY + " to " + (residentialObject.getY() + residentialObject.getHeight()));
			if (fromY + getHeight() + leeway > (residentialObject.getY()) &&
					toY < residentialObject.getY() + residentialObject.getHeight()) {
				pathPoints++;
				stopAtResidential = true;
			}

			// Now add the path points in the return value
			path = new Path(pathPoints);
			path.to(fromX - getWidth() / 2, fromY - getHeight() / 2);
			if (stopAtBottom)
				addLandmarkToPath(path, bottomObject);
			if (stopAtMiddle)
				addLandmarkToPath(path, middleObject);
			if (stopAtTop)
				addLandmarkToPath(path, topObject);
			if (stopAtResidential)
				addLandmarkToPath(path, residentialObject);
			path.to(toX - getWidth() / 2, toY - getHeight() / 2);
		}
		
		return path;
	}

	private void addLandmarkToPath(final Path path, final TMXObject landmark) {
		path.to(landmark.getX() + MathUtils.random(0, landmark.getWidth()) - getWidth() / 2, 
				landmark.getY() + MathUtils.random(0, landmark.getHeight()) - getHeight() / 2);
	}
	
	private void faceDownward() {
		this.animate(animationDurations, 0, 7, true);
	}
	
	private void faceRight() {
		this.animate(animationDurations, 24, 31, true);
	}
	
	private void faceUpward() {
		this.animate(animationDurations, 8, 15, true);
	}
	
	private void faceLeft() {
		this.animate(animationDurations, 16, 23, true);
	}

	private void reachedDestination() {
		//this.isWalking = false;
		
		//faceUpward();
		//setVisible(false);
		scene.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				activity.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						scene.getLastChild().detachChild(FluVilleResident.this);
						isWalking = false;
						if (!infected && !immunized && hoursOfSanitizerRemaining < 1 && 
								!currentDestination.getName().equals(home.getName()) && 
								activity.isBuildingInfected(currentDestination))
							infect();
					}
				});
			}
		}));
	}
	
	private float getRandomSpeed() {
		return MathUtils.random(10.0f, 15.0f);
	}
	
	public void setPlaceOfWork(final TMXObject placeOfWork) {
		/*Log.d(TAG, "Setting place of work X: " + placeOfWork.getX() + 
				", place of work width: " + placeOfWork.getWidth());*/
		this.placeOfWork = placeOfWork;
	}
	
	public boolean isAtHome() {
		Rectangle homeRectangle = FluVilleCityActivity.RECTANGLE_POOL.obtain(home.getX(), home.getY(), 
				home.getWidth(), home.getHeight());
		boolean collides = homeRectangle.collidesWith(this);
		FluVilleCityActivity.RECTANGLE_POOL.recyclePoolItem(homeRectangle);
		return collides;
	}
	
	public boolean isAtWork() {
		Rectangle workRectangle = FluVilleCityActivity.RECTANGLE_POOL.obtain(placeOfWork.getX(), placeOfWork.getY(), 
				placeOfWork.getWidth(), placeOfWork.getHeight());
		boolean collides = workRectangle.collidesWith(this);
		FluVilleCityActivity.RECTANGLE_POOL.recyclePoolItem(workRectangle);
		return collides;
	}
	
	public void sendHome() {
		//this.unregisterEntityModifier(this.pathOfTravel);
		
		// Remove all entity modifiers
		this.unregisterEntityModifiers(new IEntityModifierMatcher() {
			
			@Override
			public boolean matches(IModifier<IEntity> pObject) {
				// Unregister ALL modifiers
				return true;
			}
		});
		
		if (this.infected) {
			this.wasSentHomeSick = true;
		}
		
		Path path = new Path(2);
		path.to(getX() - getWidth() / 2, getY() - getHeight() / 2).
			to(home.getX() + MathUtils.random(0, home.getWidth()) - getWidth() / 2, 
				home.getY() + MathUtils.random(0, home.getHeight()) - getHeight() / 2);
		registerEntityModifier(new PathModifier(0.5f, path, null, homePathListener));
	}
	
	public void immunize() {
		immunized = true;
		setTextureRegion(activity.mImmunizedPlayerTextureRegion);
		detachChild(protectionLabel);
	}
	
	public void applyHandSanitizer() {
		hoursOfSanitizerRemaining += HOURS_OF_PROTECTION_FROM_HAND_SANITIZER;
		protectionLabel.setText("" + hoursOfSanitizerRemaining);
		attachChild(protectionLabel);
	}
	
	public void reduceSanitizerProtection() {
		hoursOfSanitizerRemaining--;
		if (hoursOfSanitizerRemaining > 0) {
			protectionLabel.setText("" + hoursOfSanitizerRemaining);
		}
		else {
			detachChild(protectionLabel);
		}
	}
	
	public void removeSanitizerProtection() {
		detachChild(protectionLabel);
	}
	
	public void recover() {
		this.infected = false;
		setTextureRegion(activity.mPlayerTextureRegion);
	}
	
	public void infect() {
		this.infected = true;
		this.daysOfInfectionRemaining = MathUtils.random(MIN_DAYS_INFECTED, MAX_DAYS_INFECTED);
		setTextureRegion(activity.mInfectedPlayerTextureRegion);
		detachChild(protectionLabel);
	}
	
	private class MainPathListener implements IPathModifierListener {
		@Override
		public void onWaypointPassed(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
			float xPoints[] = pPathModifier.getPath().getCoordinatesX();
			float yPoints[] = pPathModifier.getPath().getCoordinatesY();
			if (pWaypointIndex >= (xPoints.length-1)) {
				reachedDestination();
				if (infected && !currentDestination.getName().equals(home.getName()))
					activity.infectBuilding(currentDestination);
			}
			else {
				float xDifference = xPoints[pWaypointIndex + 1] - xPoints[pWaypointIndex];
				float yDifference = yPoints[pWaypointIndex + 1] - yPoints[pWaypointIndex];
				if (Math.abs(xDifference) > Math.abs(yDifference) &&
						(xDifference > 0)) {
					faceRight();
				}
				else if (Math.abs(xDifference) > Math.abs(yDifference) &&
						(xDifference < 0)) {
					faceLeft();
				}
				else if (Math.abs(yDifference) > Math.abs(xDifference) &&
						(yDifference < 0)) {
					faceUpward();
				}
				else if (Math.abs(yDifference) > Math.abs(xDifference) &&
						(yDifference > 0)) {
					faceDownward();
				}
			}
		}
	}
	
	private class HomePathListener implements IPathModifierListener {
		@Override
		public void onWaypointPassed(PathModifier pPathModifier, IEntity pEntity,
				int pWaypointIndex) {
			float xPoints[] = pPathModifier.getPath().getCoordinatesX();
			if (pWaypointIndex >= (xPoints.length-1)) {
				reachedDestination();
			}
		}
	}
}
