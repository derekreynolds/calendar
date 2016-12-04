package com.evolvingreality.onleave.calendar.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Calendar.
 */
@Entity
@Table(name = "calendar")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Calendar extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "country")
    private String country;
    
    @NotNull
    @Column(name = "year")
    private Integer year;
    
    @OrderBy("holidayDate ASC")
    @OneToMany(mappedBy = "calendar")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Holiday> holidays = new HashSet<>();

    
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Calendar calendar = (Calendar) o;
        if(calendar.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, calendar.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Calendar{" +
            "id=" + id +
            ", country='" + country + "'" +            
            ", year='" + year + "'" +
            '}';
    }
}
