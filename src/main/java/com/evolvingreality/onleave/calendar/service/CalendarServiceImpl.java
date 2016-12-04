package com.evolvingreality.onleave.calendar.service;

import com.evolvingreality.onleave.calendar.domain.Calendar;
import com.evolvingreality.onleave.calendar.domain.CalendarDay;
import com.evolvingreality.onleave.calendar.domain.CalendarMonth;
import com.evolvingreality.onleave.calendar.domain.CalendarWeek;
import com.evolvingreality.onleave.calendar.domain.CalendarYear;
import com.evolvingreality.onleave.calendar.domain.DayType;
import com.evolvingreality.onleave.calendar.domain.Holiday;
import com.evolvingreality.onleave.calendar.repository.CalendarRepository;
import com.evolvingreality.onleave.calendar.repository.HolidayRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;



/**
 * Service Implementation for managing Calendar.
 */
@Service
@Transactional(readOnly = true)
public class CalendarServiceImpl implements CalendarService {

    private final Logger log = LoggerFactory.getLogger(CalendarServiceImpl.class);    

    private final CalendarRepository calendarRepository;
    
    private final HolidayRepository holidayRepository;
    
    @Autowired
    public CalendarServiceImpl(final CalendarRepository calendarRepository, final HolidayRepository holidayRepository) {
    	this.calendarRepository = calendarRepository;
    	this.holidayRepository = holidayRepository;
    }
    
    /**
     * Save a calendar.
     * 
     * @param calendar the entity to save
     * @return the persisted entity
     */
    @Transactional(readOnly = false) 
    public Calendar save(Calendar calendar) {
        log.debug("Request to save Calendar : {}", calendar);
        return calendarRepository.save(calendar);
    }

    /**
     *  Get all the calendars.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    public Page<Calendar> findAll(Pageable pageable) {
        log.debug("Request to get all Calendars");
        return calendarRepository.findAll(pageable); 
    }

    /**
     *  Get one calendar by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public Calendar findOne(Long id) {
        log.debug("Request to get Calendar : {}", id);
        return calendarRepository.findOne(id);
    }
    
    @Override
   	public Optional<CalendarYear> getPaddedCalendarYear(String country, Integer year) {
       	log.debug("Request to get Year : {}, {}", country, year);
       	
       	Optional<CalendarYear> calendarYear = getCalendarYear(country, year);
       	
       	calendarYear.ifPresent(c -> c.getMonths().forEach(m -> padCalendarMonth(c, m)));
       	
   		return calendarYear;
   	}
    
    private void padCalendarMonth(CalendarYear calendarYear, CalendarMonth calendarMonth) {
    	   	
    	padStartMonth(calendarYear, calendarMonth);
    	padEndMonth(calendarYear, calendarMonth);
    	
    }
    
    private void padStartMonth(CalendarYear calendarYear, CalendarMonth calendarMonth) {
    	
    	LocalDate monthStartDate = LocalDate.of(calendarYear.getYear(), calendarMonth.getMonth(), 1);
    	
    	while(monthStartDate.getDayOfWeek() != DayOfWeek.MONDAY) {
    		monthStartDate = monthStartDate.minusDays(1);
    		CalendarDay day = getDay(monthStartDate);
    		day.setDayType(DayType.PADDED);
    		calendarMonth.getWeeks().get(0).getDays().add(0, day);    		
    	}
    }
    
    private void padEndMonth(CalendarYear calendarYear, CalendarMonth calendarMonth) {
    	
    	CalendarWeek lastWeek = calendarMonth.getWeeks().get(calendarMonth.getWeeks().size() - 1);
    	CalendarDay calendarDay = lastWeek.getDays().get(lastWeek.getDays().size() - 1);
    	LocalDate monthEndDate = LocalDate.of(calendarYear.getYear(), calendarMonth.getMonth(), calendarDay.getDate().getDayOfMonth());
    	
    	while(monthEndDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
    		monthEndDate = monthEndDate.plusDays(1);
    		CalendarDay day = getDay(monthEndDate);
    		day.setDayType(DayType.PADDED);
    		lastWeek.getDays().add(day);    		
    	}
    }
    
    @Override
	public Optional<CalendarYear> getCalendarYear(String country, Integer year) {
    	log.debug("Request to get Year : {}, {}", country, year);
    	
    	Optional<Calendar> calendar = calendarRepository.findByCountryAndYear(country, year);
    	
    	Optional<CalendarYear> calendarYear = Optional.empty();
    	
    	if(calendar.isPresent()) {
    		
    		Calendar c = calendar.get();
    		CalendarYear cy = new CalendarYear();
        	
        	cy.setCountry(country);
        	cy.setYear(c.getYear());        	
        	cy.setMonths(getMonths(c.getYear()));
        	
    		return Optional.of(cy);
    	}
    	
		return calendarYear;
	}

	/**
     *  Delete the  calendar by id.
     *  
     *  @param id the id of the entity
     */
    @Transactional(readOnly = false) 
    public void delete(Long id) {
        log.debug("Request to delete Calendar : {}", id);
        calendarRepository.delete(id);
    }
    
    @Override
	public LocalDate getNextWorkDay(LocalDate date) {
    	log.debug("Request to get next work date : {}", date);
    	
    	do {    		
    		date = date.plusDays(1);    		
    	} while (isWeekEnd(date) || holidayRepository.findByHolidayDate(date).isPresent());
    	
		return date;
	}

	@Override
	public List<Calendar> findUniqueCountryCalendar() {
		
		return calendarRepository.findAll()
				.stream()
				.filter(distinctByKey(c -> c.getCountry()))
				.collect(Collectors.toList());
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private List<CalendarMonth> getMonths(Integer year) {
    	
    	List<CalendarMonth> months = new ArrayList<>();
    	
    	LocalDate startDate = LocalDate.of(year, Month.JANUARY, 1);
    	LocalDate endDate = LocalDate.of(year, Month.DECEMBER, 31);
    	
    	do {    		
    		months.add(getMonth(startDate));
    		startDate = startDate.plusMonths(1);    		
    	} while (startDate.isBefore(endDate));
    	    	    	
    	return months;
    }
    
    private CalendarMonth getMonth(LocalDate monthStartDate) {
    	
    	CalendarMonth month = new CalendarMonth();
    	
    	month.setMonth(monthStartDate.getMonth());
    	LocalDate nextWeek = monthStartDate;
    	
    	while(monthStartDate.getMonth() == nextWeek.getMonth()) {
    		CalendarWeek week = getWeek(nextWeek);
    		month.getWeeks().add(week);
    		nextWeek = nextWeek.plusDays(week.getDays().size());
    	}
    	
    	return month;
    }
    
    private CalendarWeek getWeek(LocalDate weekStartDate) {
    	
    	CalendarWeek week = new CalendarWeek();
    	
    	LocalDate nextDay = weekStartDate;
    	
    	int weekOfYear = weekStartDate.get(WeekFields.ISO.weekOfYear());
    	
    	week.setOrdinal(weekOfYear);
    	
    	while((week.getOrdinal() == weekOfYear) && (weekStartDate.getMonth() == nextDay.getMonth())) {     		
    		week.getDays().add(getDay(nextDay));
    		nextDay = nextDay.plusDays(1);
    		weekOfYear = nextDay.get(WeekFields.ISO.weekOfYear());
    	}
    	
    	return week;
    }
    
    private CalendarDay getDay(LocalDate date) {
    	
    	CalendarDay calendarDay = new CalendarDay();    		
    	
    	calendarDay.setDate(date);
    	calendarDay.setDayOfWeek(date.getDayOfWeek());    	
    	setDayType(calendarDay, date);
    	
    	return calendarDay;
    }
    
    private void setDayType(CalendarDay calendarDay, LocalDate date) {
    	
    	Optional<Holiday> holiday = holidayRepository.findByHolidayDate(date);
    	
    	if(isWeekEnd(date))
    		calendarDay.setDayType(DayType.WEEKEND);
    	else if(holiday.isPresent()) {
    		holiday.ifPresent(h -> {
    			calendarDay.setDayType(DayType.HOLIDAY);
    			calendarDay.setHolidayName(h.getName());
    			calendarDay.setDescription(h.getDescription());
    		});
    	} else {
    		calendarDay.setDayType(DayType.WEEKDAY);
    	}
    	
    }
    
    private boolean isWeekEnd(LocalDate date) {
    	
    	if((date.getDayOfWeek() == DayOfWeek.SATURDAY) || (date.getDayOfWeek() == DayOfWeek.SUNDAY))
    		return true;
    				
    	return false;
    }
}
