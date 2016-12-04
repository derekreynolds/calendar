package com.evolvingreality.onleave.calendar.domain;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;


public class CalendarMonth {

	private Month month;
	
	private List<CalendarWeek> weeks = new ArrayList<>();	
	
	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public List<CalendarWeek> getWeeks() {
		return weeks;
	}

	public void setDays(List<CalendarWeek> weeks) {
		this.weeks = weeks;
	}
	
	
}
