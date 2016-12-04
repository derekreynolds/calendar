package com.evolvingreality.onleave.calendar.service;

import com.evolvingreality.onleave.calendar.domain.Calendar;
import com.evolvingreality.onleave.calendar.domain.CalendarYear;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service Interface for managing Calendar.
 */
public interface CalendarService {

    /**
     * Save a calendar.
     * 
     * @param calendar the entity to save
     * @return the persisted entity
     */
    Calendar save(Calendar calendar);

    /**
     *  Get all the calendars.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Calendar> findAll(Pageable pageable);
    
    /**
     * Get the unique country calendar. 
     * @return
     */
    List<Calendar> findUniqueCountryCalendar();

    /**
     *  Get the "id" calendar.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Calendar findOne(Long id);
    
    /**
     * Gets the {@link CalendarYear} for the calendar. Contains all the days
     * weeks/months of the year.
     * 
     * @param country the country calendar
     * @param year the year of the calendar
     * @return a {@link CalendarYear} containing every month/week/day of the year
     */
    Optional<CalendarYear> getCalendarYear(String country, Integer year);

    /**
     * Gets the {@link CalendarYear} for the calendar. Contains all the days
     * for the year.
     * 
     * @param country the country calendar
     * @param year the year of the calendar
     * @return a {@link CalendarYear} containing every month/week/day of the year  
     */
    Optional<CalendarYear> getPaddedCalendarYear(String country, Integer year);
    
    /**
     *  Delete the "id" calendar.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Get the next work day based on the date passed in.
     * 
     * @return {@link LocalDate} the next work day.
     */
    LocalDate getNextWorkDay(LocalDate date);
    
}
