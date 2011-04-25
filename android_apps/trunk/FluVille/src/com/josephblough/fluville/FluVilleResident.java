package com.josephblough.fluville;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.BaseSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.MathUtils;

import android.util.Log;

public class FluVilleResident extends AnimatedSprite {

	private static final String TAG = "FluVilleResident";

	private FluVilleCityActivity activity;
	private Scene scene;
	
	public TMXObject home;
	public TMXObject placeOfWork;
	
	// state variables
	public boolean infected;
	public boolean immunized;
	public boolean hasFaceMask;
	public int hoursOfSanitizerRemaining;
	public int daysOfInfectionRemaining;
	
	public FluVilleResident(final FluVilleCityActivity activity, final Scene scene, final TMXObject origin, final TiledTextureRegion texture) {
		super(origin.getX() + origin.getWidth() / 2 - texture.getTileWidth() / 2,
				origin.getY() + origin.getHeight() / 2 - texture.getTileHeight() / 2, texture.clone());
		
		Log.d(TAG, "Creating FluVilleResident");
		this.activity = activity;
		this.scene = scene;
		
		this.home = origin;
		this.infected = false;
		this.immunized = false;
		this.hasFaceMask = false;
		this.hoursOfSanitizerRemaining = 0;
		this.daysOfInfectionRemaining = 0;
		
	}
	
	public void setDestination(final TMXObject destination) {
		final Path path = calculatePath(this, this.getX(), this.getY(), 
				destination.getX() + MathUtils.random(0, destination.getWidth()), destination.getY());

		this.registerEntityModifier(new PathModifier(getRandomSpeed(), path, null, new IPathModifierListener() {
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
		}));
	}

	private Path calculatePath(final BaseSprite actor, final float fromX, final float fromY, final float toX, final float toY) {
		boolean leftPath = fromX < FluVilleCityActivity.CAMERA_WIDTH / 2;
		boolean goingDown = fromY < toY;
		boolean stopAtResidential = false;
		boolean stopAtTop = false;
		boolean stopAtMiddle = false;
		boolean stopAtBottom = false;
		int pathPoints = 2;
		Path path = null;
		final int leeway = 10;
		if (goingDown) {
			TMXObject residentialObject = null;
			TMXObject topObject = null;
			TMXObject middleObject = null;
			TMXObject bottomObject = null;
			
			// Find the points on the map
			if (leftPath) {
				// Check the points in the following order:
				//MAP_LANDMARK_LEFT_RESIDENTIAL_INTERSECTION
				residentialObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_LEFT_RESIDENTIAL_INTERSECTION);
				//MAP_LANDMARK_TOP_LEFT_INTERSECTION
				topObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_TOP_LEFT_INTERSECTION);
				//MAP_LANDMARK_MIDDLE_LEFT_INTERSECTION
				middleObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_MIDDLE_LEFT_INTERSECTION);
				//MAP_LANDMARK_BOTTOM_LEFT_INTERSECTION
				bottomObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_BOTTOM_LEFT_INTERSECTION);
			}
			else {
				// Check the points in the following order:
				//MAP_LANDMARK_RIGHT_RESIDENTIAL_INTERSECTION
				residentialObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_RIGHT_RESIDENTIAL_INTERSECTION);
				//MAP_LANDMARK_TOP_RIGHT_INTERSECTION
				topObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_TOP_RIGHT_INTERSECTION);
				//MAP_LANDMARK_MIDDLE_RIGHT_INTERSECTION
				middleObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_MIDDLE_RIGHT_INTERSECTION);
				//MAP_LANDMARK_BOTTOM_RIGHT_INTERSECTION
				bottomObject = activity.findLandmark(FluVilleCityActivity.MAP_LANDMARK_BOTTOM_RIGHT_INTERSECTION);
			}
			
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
			path.to(fromX - actor.getWidth() / 2, fromY - actor.getHeight() / 2);
			if (stopAtResidential)
				addLandmarkToPath(actor, path, residentialObject);
			if (stopAtTop)
				addLandmarkToPath(actor, path, topObject);
			if (stopAtMiddle)
				addLandmarkToPath(actor, path, middleObject);
			if (stopAtBottom)
				addLandmarkToPath(actor, path, bottomObject);
			path.to(toX - actor.getWidth() / 2, toY - actor.getHeight() / 2);
		}
		else {
			if (leftPath) {
				// Check the points in the following order:
				//MAP_LANDMARK_BOTTOM_LEFT_INTERSECTION
				//MAP_LANDMARK_MIDDLE_LEFT_INTERSECTION
				//MAP_LANDMARK_TOP_LEFT_INTERSECTION
				//MAP_LANDMARK_LEFT_RESIDENTIAL_INTERSECTION
			}
			else {
				// Check the points in the following order:
				//MAP_LANDMARK_BOTTOM_RIGHT_INTERSECTION
				//MAP_LANDMARK_MIDDLE_RIGHT_INTERSECTION
				//MAP_LANDMARK_TOP_RIGHT_INTERSECTION
				//MAP_LANDMARK_RIGHT_RESIDENTIAL_INTERSECTION
			}
		}
		
		return path;
	}

	private void addLandmarkToPath(final BaseSprite actor, final Path path, final TMXObject landmark) {
		path.to(landmark.getX() + MathUtils.random(0, landmark.getWidth()) - actor.getWidth() / 2, 
				landmark.getY() + MathUtils.random(0, landmark.getHeight()) - actor.getHeight() / 2);
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
		faceUpward();
		//setVisible(false);
		scene.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				activity.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						scene.getLastChild().detachChild(FluVilleResident.this);
					}
				});
			}
		}));
	}
	
	private float getRandomSpeed() {
		return MathUtils.random(10.0f, 15.0f);
	}
}
