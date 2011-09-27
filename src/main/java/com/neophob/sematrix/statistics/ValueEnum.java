package com.neophob.sematrix.statistics;

import org.apache.commons.lang.Validate;

public enum ValueEnum {
	GENERATORS_UPDATE("average generators update duration"),
	GENERATORS_UPDATE_EFFECTIVE("effective generators update duration"),
	EFFECTS_UPDATE("average effects update duration"),
	EFFECTS_UPDATE_EFFECTIVE("effective effects update duration"),
	OUTPUTS_UPDATE("average outputs update duration"),
	OUTPUTS_UPDATE_EFFECTIVE("effective outputs update duration");

	private String description;
	
	private ValueEnum(String description) {
		Validate.notNull(description);
		Validate.notEmpty(description);
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
}
