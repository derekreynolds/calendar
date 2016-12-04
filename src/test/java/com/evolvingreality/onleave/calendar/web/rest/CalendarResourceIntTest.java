package com.evolvingreality.onleave.calendar.web.rest;

import com.evolvingreality.onleave.calendar.CalendarApp;
import com.evolvingreality.onleave.calendar.domain.Calendar;
import com.evolvingreality.onleave.calendar.repository.CalendarRepository;
import com.evolvingreality.onleave.calendar.service.CalendarService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CalendarResource REST controller.
 *
 * @see CalendarResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CalendarApp.class)
@WebAppConfiguration
@IntegrationTest
public class CalendarResourceIntTest {

    private static final String DEFAULT_COUNTRY = "IE";
    private static final String UPDATED_COUNTRY = "UK";

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    @Inject
    private CalendarRepository calendarRepository;

    @Inject
    private CalendarService calendarService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCalendarMockMvc;

    private Calendar calendar;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CalendarResource calendarResource = new CalendarResource(calendarService);

        this.restCalendarMockMvc = MockMvcBuilders.standaloneSetup(calendarResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        calendar = new Calendar();
        calendar.setCountry(DEFAULT_COUNTRY);
        calendar.setYear(DEFAULT_YEAR);
    }

    @Test
    @Transactional
    public void createCalendar() throws Exception {
        int databaseSizeBeforeCreate = calendarRepository.findAll().size();

        // Create the Calendar

        restCalendarMockMvc.perform(post("/api/calendars")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(calendar)))
                .andExpect(status().isCreated());

        // Validate the Calendar in the database
        List<Calendar> calendars = calendarRepository.findAll();
        assertThat(calendars).hasSize(databaseSizeBeforeCreate + 1);
        Calendar testCalendar = calendars.get(calendars.size() - 1);
        assertThat(testCalendar.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testCalendar.getYear()).isEqualTo(DEFAULT_YEAR);
    }

    @Test
    @Transactional
    public void getAllCalendars() throws Exception {
        // Initialize the database
        calendarRepository.saveAndFlush(calendar);

        // Get all the calendars
        restCalendarMockMvc.perform(get("/api/calendars?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(calendar.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_COUNTRY.toString())))
                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)));
    }

    @Test
    @Transactional
    public void getCalendar() throws Exception {
        // Initialize the database
        calendarRepository.saveAndFlush(calendar);

        // Get the calendar
        restCalendarMockMvc.perform(get("/api/calendars/{id}", calendar.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(calendar.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR));
    }

    @Test
    @Transactional
    public void getNonExistingCalendar() throws Exception {
        // Get the calendar
        restCalendarMockMvc.perform(get("/api/calendars/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCalendar() throws Exception {
        // Initialize the database
        calendarService.save(calendar);

        int databaseSizeBeforeUpdate = calendarRepository.findAll().size();

        // Update the calendar
        Calendar updatedCalendar = new Calendar();
        updatedCalendar.setId(calendar.getId());
        updatedCalendar.setCountry(UPDATED_COUNTRY);
        updatedCalendar.setYear(UPDATED_YEAR);

        restCalendarMockMvc.perform(put("/api/calendars")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCalendar)))
                .andExpect(status().isOk());

        // Validate the Calendar in the database
        List<Calendar> calendars = calendarRepository.findAll();
        assertThat(calendars).hasSize(databaseSizeBeforeUpdate);
        Calendar testCalendar = calendars.get(calendars.size() - 1);
        assertThat(testCalendar.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testCalendar.getYear()).isEqualTo(UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void deleteCalendar() throws Exception {
        // Initialize the database
        calendarService.save(calendar);

        int databaseSizeBeforeDelete = calendarRepository.findAll().size();

        // Get the calendar
        restCalendarMockMvc.perform(delete("/api/calendars/{id}", calendar.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Calendar> calendars = calendarRepository.findAll();
        assertThat(calendars).hasSize(databaseSizeBeforeDelete - 1);
    }
}
