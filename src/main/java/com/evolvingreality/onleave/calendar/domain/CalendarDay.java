package com.evolvingreality.onleave.calendar.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class CalendarDay {

	private LocalDate date;
	
	private DayType dayType;
	
	private DayOfWeek dayOfWeek;
	
	private String holidayName;
	
	private String description;

	
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public DayType getDayType() {
		return dayType;
	}

	public void setDayType(DayType dayType) {
		this.dayType = dayType;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
