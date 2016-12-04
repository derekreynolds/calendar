package com.evolvingreality.onleave.calendar.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CalendarYear {

    private Long id;

    private String country;
    
    private Integer year;
    
    private List<CalendarMonth> months = new ArrayList<>();

    
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<CalendarMonth> getMonths() {
		return months;
	}

	public void setMonths(List<CalendarMonth> months) {
		this.months = months;
	}
    
    
}
