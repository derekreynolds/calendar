package com.evolvingreality.onleave.calendar.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarWeek {

	private Integer ordinal;
	
	private List<CalendarDay> days = new ArrayList<>();
	
	
	public Integer getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

	public List<CalendarDay> getDays() {
		return days;
	}

	public void setDays(List<CalendarDay> days) {
		this.days = days;
	}
	
	
	
}
