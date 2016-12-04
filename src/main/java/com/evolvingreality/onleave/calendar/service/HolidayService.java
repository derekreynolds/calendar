package com.evolvingreality.onleave.calendar.service;

import com.evolvingreality.onleave.calendar.domain.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



/**
 * Service Interface for managing Holiday.
 */
public interface HolidayService {

    /**
     * Save a holiday.
     * 
     * @param holiday the entity to save
     * @return the persisted entity
     */
    Holiday save(Holiday holiday);

    /**
     *  Get all the holidays.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Holiday> findAll(Pageable pageable);

    /**
     *  Get the "id" holiday.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Holiday findOne(Long id);

    /**
     *  Delete the "id" holiday.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);
}
