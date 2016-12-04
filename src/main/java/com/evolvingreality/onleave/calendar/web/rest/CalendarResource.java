package com.evolvingreality.onleave.calendar.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.evolvingreality.onleave.calendar.domain.Calendar;
import com.evolvingreality.onleave.calendar.domain.CalendarDay;
import com.evolvingreality.onleave.calendar.domain.CalendarYear;
import com.evolvingreality.onleave.calendar.service.CalendarService;
import com.evolvingreality.onleave.calendar.web.rest.util.HeaderUtil;
import com.evolvingreality.onleave.calendar.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Calendar.
 */
@RestController
@RequestMapping("/api")
public class CalendarResource {

    private final Logger log = LoggerFactory.getLogger(CalendarResource.class);
           
    private final CalendarService calendarService;
    
    @Autowired
    public CalendarResource(final CalendarService calendarService) {
    	this.calendarService = calendarService;
    }
    
    /**
     * POST  /calendars : Create a new calendar.
     *
     * @param calendar the calendar to create
     * @return the ResponseEntity with status 201 (Created) and with body the new calendar, or with status 400 (Bad Request) if the calendar has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/calendars",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Calendar> createCalendar(@RequestBody Calendar calendar) throws URISyntaxException {
        log.debug("REST request to save Calendar : {}", calendar);
        if (calendar.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("calendar", "idexists", "A new calendar cannot already have an ID")).body(null);
        }
        Calendar result = calendarService.save(calendar);
        return ResponseEntity.created(new URI("/api/calendars/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("calendar", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /calendars : Updates an existing calendar.
     *
     * @param calendar the calendar to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated calendar,
     * or with status 400 (Bad Request) if the calendar is not valid,
     * or with status 500 (Internal Server Error) if the calendar couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/calendars",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Calendar> updateCalendar(@RequestBody Calendar calendar) throws URISyntaxException {
        log.debug("REST request to update Calendar : {}", calendar);
        if (calendar.getId() == null) {
            return createCalendar(calendar);
        }
        Calendar result = calendarService.save(calendar);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("calendar", calendar.getId().toString()))
            .body(result);
    }

    /**
     * GET  /calendars : get all the calendars.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of calendars in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/calendars",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Calendar>> getAllCalendars(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Calendars");
        Page<Calendar> page = calendarService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/calendars");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /calendars/:id : get the "id" calendar.
     *
     * @param id the id of the calendar to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the calendar, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/calendars/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Calendar> getCalendar(@PathVariable Long id) {
        log.debug("REST request to get Calendar : {}", id);
        Calendar calendar = calendarService.findOne(id);
        return Optional.ofNullable(calendar)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * GET  /calendars/countries : get the calendar.
     *
     * @param id the id of the calendar to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the calendar
     */
    @RequestMapping(value = "/calendars/countries",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Calendar>> getCalendarCountries() {
        log.debug("REST request to get Calendar Countries");
        
        return new ResponseEntity<>(calendarService.findUniqueCountryCalendar(), HttpStatus.OK);
    }
    
    /**
     * GET  /calendars/country/:country/year/:year : get the "year" calendar.
     *
     * @param id the id of the calendar to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the calendar, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/calendars/country/{country}/year/{year}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CalendarYear> getCompleteCalendar(@PathVariable String country, @PathVariable Integer year) {
        log.debug("REST request to get Calendar : {},{}", country, year);
        Optional<CalendarYear> calendarYear = calendarService.getCalendarYear(country, year);
        return calendarYear
        	.map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * GET  /calendars/country/:country/year/:year/padded : get the "year" calendar.
     *
     * @param id the id of the calendar to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the calendar, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/calendars/country/{country}/year/{year}/padded",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CalendarYear> getCompletePaddedCalendar(@PathVariable String country, @PathVariable Integer year) {
        log.debug("REST request to get Calendar : {},{}", country, year);
        Optional<CalendarYear> calendarYear = calendarService.getPaddedCalendarYear(country, year);
        return calendarYear
        	.map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /calendars/:id : delete the "id" calendar.
     *
     * @param id the id of the calendar to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/calendars/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCalendar(@PathVariable Long id) {
        log.debug("REST request to delete Calendar : {}", id);
        calendarService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("calendar", id.toString())).build();
    }
    
    
    /**
     * GET  /calendars/next/work/day/:date : get the "next work date" calendar.
     *
     * @param date the date we want the next work day from.
     * @return the ResponseEntity with status 200 (OK) and with body the date
     */
    @RequestMapping(value = "/calendars/next/work/day/{date}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CalendarDay> getNextWorkDay(@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        log.debug("REST request to get next work date : {}", date);
        LocalDate nextWorkDate = calendarService.getNextWorkDay(LocalDateTime.ofInstant(
        		date.toInstant(), ZoneId.systemDefault()).toLocalDate());
        CalendarDay day = new CalendarDay();
        day.setDate(nextWorkDate);
        return new ResponseEntity<>(day, HttpStatus.OK);
    }

}
