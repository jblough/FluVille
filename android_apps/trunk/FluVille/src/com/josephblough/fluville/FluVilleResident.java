package com.josephblough.fluville;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.MathUtils;

import android.util.Log;

public class FluVilleResident extends AnimatedSprite {

	private static final String TAG = "FluVilleResident";

	private static final int HOURS_OF_PROTECTION_FROM_HAND_SANITIZER = 5;
	
	private FluVilleCityActivity activity;
	private Scene scene;
	
	public TMXObject home;
	public TMXObject placeOfWork;
	
	// state variables
	public boolean infected;
	public boolean immunized;
	public int hoursOfSanitizerRemaining;
	public int daysOfInfectionRemaining;
	public PathModifier pathOfTravel;
	public boolean isWalking;
	public ChangeableText protectionLabel;
	
	public FluVilleResident(final FluVilleCityActivity activity, final Scene scene, final TMXObject origin, final TiledTextureRegion texture) {
		super(origin.getX() + origin.getWidth() / 2 - texture.getTileWidth() / 2,
				origin.getY() + origin.getHeight() / 2 - texture.getTileHeight() / 2, texture.clone());
		
		Log.d(TAG, "Creating FluVilleResident");
		this.activity = activity;
		this.scene = scene;
		
		this.home = origin;
		this.infected = false;
		this.immunized = false;
		this.hoursOfSanitizerRemaining = 0;
		this.daysOfInfectionRemaining = 0;
		this.isWalking = false;
		
		this.protectionLabel = new ChangeableText(0, 0, this.activity.mMenuFont, "10");
	}

	public void walk() {
		//Log.d(TAG, "walk");
		walk(this.placeOfWork);
	}
	
	public void walk(final TMXObject destination) {
		//Log.d(TAG, "walk to destination");
		this.isWalking = true;
		
		final Path path = calculatePath(getX(), getY(), 
				destination.getX() + MathUtils.random(0, destination.getWidth()), destination.getY());

		this.pathOfTravel = new PathModifier(getRandomSpeed(), path, null, new IPathModifierListener() {
			@Override
			public void onWaypointPassed(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				float xPoints[] = pPathModifier.getPath().getCoordinatesX();
				float yPoints[] = pPathModifier.getPath().getCoordinatesY();
				if (pWaypointIndex >= (xPoints.length-1)) {
					reachedDestination();
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
		});
		
		this.registerEntityModifier(this.pathOfTravel);
	}

	private Path calculatePath(final float fromX, final float fromY, final float toX, final float toY) {
		boolean leftPath = fromX < FluVilleCityActivity.CAMERA_WIDTH / 2;
		boolean goingDown = fromY < toY;
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
		//this.animate(new long[]{200, 200, 200}, 6, 8, true);
		this.animate(new long[]{200, 200, 200, 200, 200, 200, 200, 200}, 0, 7, true);
	}
	
	private void faceRight() {
		//this.animate(new long[]{200, 200, 200}, 3, 5, true);
		this.animate(new long[]{200, 200, 200, 200, 200, 200, 200, 200}, 24, 31, true);
	}
	
	private void faceUpward() {
		//this.animate(new long[]{200, 200, 200}, 0, 2, true);
		this.animate(new long[]{200, 200, 200, 200, 200, 200, 200, 200}, 8, 15, true);
	}
	
	private void faceLeft() {
		//this.animate(new long[]{200, 200, 200}, 9, 11, true);
		this.animate(new long[]{200, 200, 200, 200, 200, 200, 200, 200}, 16, 23, true);
	}

	private void reachedDestination() {
		//this.isWalking = false;
		
		faceUpward();
		//setVisible(false);
		scene.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				activity.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						scene.getLastChild().detachChild(FluVilleResident.this);
						isWalking = false;
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
		Rectangle homeRectangle = new Rectangle(home.getX(), home.getY(), 
				home.getWidth(), home.getHeight());
		return homeRectangle.collidesWith(this);
	}
	
	public boolean isAtWork() {
		Rectangle workRectangle = new Rectangle(placeOfWork.getX(), placeOfWork.getY(), 
				placeOfWork.getWidth(), placeOfWork.getHeight());
		return workRectangle.collidesWith(this);
	}
	
	public void sendHome() {
		this.unregisterEntityModifier(this.pathOfTravel);
		Path path = new Path(2);
		path.to(getX() - getWidth() / 2, getY() - getHeight() / 2).
			to(home.getX() + MathUtils.random(0, home.getWidth()) - getWidth() / 2, 
				home.getY() + MathUtils.random(0, home.getHeight()) - getHeight() / 2);
		registerEntityModifier(new PathModifier(0.5f, path, null, new IPathModifierListener() {
			
			@Override
			public void onWaypointPassed(PathModifier pPathModifier, IEntity pEntity,
					int pWaypointIndex) {
				float xPoints[] = pPathModifier.getPath().getCoordinatesX();
				if (pWaypointIndex >= (xPoints.length-1)) {
					reachedDestination();
				}
			}
		}));
	}
	
	public void immunize() {
		immunized = true;
		setTextureRegion(activity.mImmunizedPlayerTextureRegion);
	}
	
	public void applyHandSanitizer() {
		hoursOfSanitizerRemaining += HOURS_OF_PROTECTION_FROM_HAND_SANITIZER;
		protectionLabel.setText("" + hoursOfSanitizerRemaining);
		attachChild(protectionLabel);
	}
	
	public void reduceSanitizerProtect() {
		hoursOfSanitizerRemaining--;
		if (hoursOfSanitizerRemaining > 0) {
			protectionLabel.setText("" + hoursOfSanitizerRemaining);
		}
		else {
			detachChild(protectionLabel);
		}
	}
	
	public void recover() {
		this.infected = false;
	}
}
