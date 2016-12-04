package com.evolvingreality.onleave.calendar.repository;

import com.evolvingreality.onleave.calendar.domain.Calendar;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Calendar entity.
 */
@SuppressWarnings("unused")
public interface CalendarRepository extends JpaRepository<Calendar,Long> {

	Optional<Calendar> findByCountryAndYear(String country, Integer year);
	
}
