package com.josephblough.fluville;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameState {

	public static final int DAYS_BETWEEN_FLU_SHOT_REFILLS = 2;
	public static final int DAYS_BETWEEN_HAND_SANITIZER_REFILLS = 1;
	
	public static final int FLU_SHOT_REFILL_SIZE = 3;
	public static final int HAND_SANITIZER_REFILL_SIZE = 5;
	public static final int MAX_FLU_SHOT_DOSES = 10;
	public static final int MAX_HAND_SANITIZER_DOSES = 10;
	
	public static final int STATE_OF_PLAY_RUNNING = 0;
	public static final int STATE_OF_PLAY_PAUSED = 1;
	
	public int day;
	public int hourOfDay;
	public int stateOfPlay;
	public int immunizationsRemaining;
	public int handSanitizerDosesRemaining;
	public List<FluVilleResident> residents;
	public Map<Integer, DaySummary> daySummaries;
	
	public boolean shownWelcomeMessage = false;
	public boolean shownImmunizationMessage = false;
	public boolean shownSanitizerMessage = false;
	public boolean shownSpongeMessage = false;
	public boolean shownSendHomeMessage = false;
	public boolean shownInfectedPersonMessage = false;
	public boolean shownInfectedBuildingMessage = false;
	public boolean shownLimitedSuppliesMessage = false;
	
	public GameState() {
		day = 1;
		hourOfDay = 0;
		immunizationsRemaining = 5;
		handSanitizerDosesRemaining = 5;
		stateOfPlay = STATE_OF_PLAY_RUNNING;
		residents = new ArrayList<FluVilleResident>();
		daySummaries = new HashMap<Integer, DaySummary>();
	}
	
	public DaySummary getDaySummary() {
		return (daySummaries.containsKey(day)) ? daySummaries.get(day) : new DaySummary();
	}

	public Bitmap graphInfectionRate() {
		final int width = 700;
		final int height = 500;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		// Perform the drawing operations via the canvas object
		Canvas canvas = new Canvas(bitmap);

		// Draw the background
		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), backgroundPaint);

		int lineSpacing = width / 5;
		Paint linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		int x = 0;
		while (x <= width) {
			canvas.drawRect(new Rect(x, 0, x+5, bitmap.getHeight()), linePaint);
			x += lineSpacing;
		}

		lineSpacing = width / 5;
		int y = 0;
		while (y <= height) {
			canvas.drawRect(new Rect(0, y, bitmap.getWidth(), y+5), linePaint);
			y += lineSpacing;
		}
		
		return bitmap;	// return the underlying bitmap
	}
}
