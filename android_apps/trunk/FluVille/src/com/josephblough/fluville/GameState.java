package com.josephblough.fluville;

import java.util.ArrayList;
import java.util.List;

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
	public int immunizationsRemaining;
	public int handSanitizerDosesRemaining;
	public List<FluVilleResident> residents;
	
	public boolean shownWelcomeMessage = false;
	public boolean shownImmunizationMessage = false;
	public boolean shownSanitizerMessage = false;
	public boolean shownSpongeMessage = false;
	public boolean shownSendHomeMessage = false;
	public boolean shownInfectedPersonMessage = false;
	public boolean shownInfectedBuildingMessage = false;
	public boolean shownLimitedSuppliesMessage = false;
	
	public int stateOfPlay;
	
	public GameState() {
		day = 1;
		hourOfDay = 0;
		immunizationsRemaining = 5;
		handSanitizerDosesRemaining = 5;
		stateOfPlay = STATE_OF_PLAY_RUNNING;
		residents = new ArrayList<FluVilleResident>();
	}
}
