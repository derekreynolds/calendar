package com.evolvingreality.onleave.calendar.web.rest;

import com.evolvingreality.onleave.calendar.CalendarApp;
import com.evolvingreality.onleave.calendar.domain.Holiday;
import com.evolvingreality.onleave.calendar.repository.HolidayRepository;
import com.evolvingreality.onleave.calendar.service.HolidayService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the HolidayResource REST controller.
 *
 * @see HolidayResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CalendarApp.class)
@WebAppConfiguration
@IntegrationTest
public class HolidayResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final LocalDate DEFAULT_HOLIDAY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_HOLIDAY_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private HolidayRepository holidayRepository;

    @Inject
    private HolidayService holidayService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restHolidayMockMvc;

    private Holiday holiday;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        HolidayResource holidayResource = new HolidayResource(holidayService);
        
        this.restHolidayMockMvc = MockMvcBuilders.standaloneSetup(holidayResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        holiday = new Holiday();
        holiday.setName(DEFAULT_NAME);
        holiday.setDescription(DEFAULT_DESCRIPTION);
        holiday.setHolidayDate(DEFAULT_HOLIDAY_DATE);
    }

    @Test
    @Transactional
    public void createHoliday() throws Exception {
        int databaseSizeBeforeCreate = holidayRepository.findAll().size();

        // Create the Holiday

        restHolidayMockMvc.perform(post("/api/holidays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(holiday)))
                .andExpect(status().isCreated());

        // Validate the Holiday in the database
        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeCreate + 1);
        Holiday testHoliday = holidays.get(holidays.size() - 1);
        assertThat(testHoliday.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testHoliday.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testHoliday.getHolidayDate()).isEqualTo(DEFAULT_HOLIDAY_DATE);
    }

    @Test
    @Transactional
    public void getAllHolidays() throws Exception {
        // Initialize the database
        holidayRepository.saveAndFlush(holiday);

        // Get all the holidays
        restHolidayMockMvc.perform(get("/api/holidays?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(holiday.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].holidayDate").value(hasItem(DEFAULT_HOLIDAY_DATE.toString())));
    }

    @Test
    @Transactional
    public void getHoliday() throws Exception {
        // Initialize the database
        holidayRepository.saveAndFlush(holiday);

        // Get the holiday
        restHolidayMockMvc.perform(get("/api/holidays/{id}", holiday.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(holiday.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.holidayDate").value(DEFAULT_HOLIDAY_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingHoliday() throws Exception {
        // Get the holiday
        restHolidayMockMvc.perform(get("/api/holidays/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHoliday() throws Exception {
        // Initialize the database
        holidayService.save(holiday);

        int databaseSizeBeforeUpdate = holidayRepository.findAll().size();

        // Update the holiday
        Holiday updatedHoliday = new Holiday();
        updatedHoliday.setId(holiday.getId());
        updatedHoliday.setName(UPDATED_NAME);
        updatedHoliday.setDescription(UPDATED_DESCRIPTION);
        updatedHoliday.setHolidayDate(UPDATED_HOLIDAY_DATE);

        restHolidayMockMvc.perform(put("/api/holidays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedHoliday)))
                .andExpect(status().isOk());

        // Validate the Holiday in the database
        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeUpdate);
        Holiday testHoliday = holidays.get(holidays.size() - 1);
        assertThat(testHoliday.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testHoliday.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testHoliday.getHolidayDate()).isEqualTo(UPDATED_HOLIDAY_DATE);
    }

    @Test
    @Transactional
    public void deleteHoliday() throws Exception {
        // Initialize the database
        holidayService.save(holiday);

        int databaseSizeBeforeDelete = holidayRepository.findAll().size();

        // Get the holiday
        restHolidayMockMvc.perform(delete("/api/holidays/{id}", holiday.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(databaseSizeBeforeDelete - 1);
    }
}
