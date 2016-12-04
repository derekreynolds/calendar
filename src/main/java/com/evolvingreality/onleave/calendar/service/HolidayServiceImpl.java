package com.evolvingreality.onleave.calendar.service;

import com.evolvingreality.onleave.calendar.domain.Holiday;
import com.evolvingreality.onleave.calendar.repository.HolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


/**
 * Service Implementation for managing Holiday.
 */
@Service
@Transactional(readOnly = true)
public class HolidayServiceImpl implements HolidayService{

    private final Logger log = LoggerFactory.getLogger(HolidayServiceImpl.class);
    
    private final HolidayRepository holidayRepository;
    
    @Autowired
    public HolidayServiceImpl(final HolidayRepository holidayRepository) {
    	this.holidayRepository = holidayRepository;
    }
    
    /**
     * Save a holiday.
     * 
     * @param holiday the entity to save
     * @return the persisted entity
     */
    @Transactional(readOnly = false)
    public Holiday save(Holiday holiday) {
        log.debug("Request to save Holiday : {}", holiday);
        return holidayRepository.save(holiday);       
    }

    /**
     *  Get all the holidays.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    public Page<Holiday> findAll(Pageable pageable) {
        log.debug("Request to get all Holidays");
        return holidayRepository.findAll(pageable); 
    }

    /**
     *  Get one holiday by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public Holiday findOne(Long id) {
        log.debug("Request to get Holiday : {}", id);
        return holidayRepository.findOne(id);
    }

    /**
     *  Delete the  holiday by id.
     *  
     *  @param id the id of the entity
     */
    @Transactional(readOnly = false)
    public void delete(Long id) {
        log.debug("Request to delete Holiday : {}", id);
        holidayRepository.delete(id);
    }
}
