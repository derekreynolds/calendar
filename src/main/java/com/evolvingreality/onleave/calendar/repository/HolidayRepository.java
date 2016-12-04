package com.evolvingreality.onleave.calendar.repository;

import com.evolvingreality.onleave.calendar.domain.Holiday;


import org.springframework.data.jpa.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Holiday entity.
 */
@SuppressWarnings("unused")
public interface HolidayRepository extends JpaRepository<Holiday,Long> {

	Optional<Holiday> findByHolidayDate(LocalDate date);
	
}
