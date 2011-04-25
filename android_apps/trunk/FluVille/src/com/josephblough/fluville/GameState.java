package com.josephblough.fluville;

import java.util.ArrayList;
import java.util.List;

public class GameState {

	public int day;
	public int hourOfDay;
	public int immunizationsRemaining;
	public int faceMasksRemaining;
	public int handSanitizerDosesRemaining;
	public List<FluVilleResident> residents;
	
	public GameState() {
		day = 1;
		hourOfDay = 0;
		immunizationsRemaining = 10;
		faceMasksRemaining = 0;
		handSanitizerDosesRemaining = 0;
		residents = new ArrayList<FluVilleResident>();
	}
}
