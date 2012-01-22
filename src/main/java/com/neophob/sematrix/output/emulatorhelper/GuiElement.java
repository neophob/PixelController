package com.neophob.sematrix.output.emulatorhelper;

public enum GuiElement {

	CURRENT_VISUAL,
	
	GENERATOR_ONE_DROPDOWN,
	GENERATOR_TWO_DROPDOWN,
	EFFECT_ONE_DROPDOWN,
	EFFECT_TWO_DROPDOWN,
	MIXER_DROPDOWN,
	
	BUTTON_RANDOM_CONFIGURATION,
	BUTTON_RANDOM_PRESENT,
	BUTTON_TOGGLE_RANDOM_MODE,
	;
	
	public static GuiElement getGuiElement(String s) {
		for (GuiElement gw: GuiElement.values()) {
			if (s.equalsIgnoreCase(gw.toString())) {
				return gw;
			}
		}
		return null;
	}
}
