package com.josephblough.fluville;

import java.util.ArrayList;
import java.util.List;

public class GameState {

	public static final int DAYS_BETWEEN_FLU_SHOT_REFILLS = 7;
	public static final int DAYS_BETWEEN_HAND_SANITIZER_REFILLS = 3;
	public static final int DAYS_BETWEEN_FACE_MASK_REFILLS = 5;
	
	public static final int FLU_SHOT_REFILL_SIZE = 5;
	public static final int HAND_SANITIZER_REFILL_SIZE = 5;
	public static final int FACE_MASK_REFILL_SIZE = 5;
	
	public int day;
	public int hourOfDay;
	public int immunizationsRemaining;
	public int handSanitizerDosesRemaining;
	public int faceMasksRemaining;
	public List<FluVilleResident> residents;
	
	public GameState() {
		day = 1;
		hourOfDay = 0;
		immunizationsRemaining = FLU_SHOT_REFILL_SIZE;
		handSanitizerDosesRemaining = HAND_SANITIZER_REFILL_SIZE;
		faceMasksRemaining = FACE_MASK_REFILL_SIZE;
		residents = new ArrayList<FluVilleResident>();
	}
}
