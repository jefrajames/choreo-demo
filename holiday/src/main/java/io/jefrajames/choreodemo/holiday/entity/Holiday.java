// Copyright 2022 jefrajames
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.jefrajames.choreodemo.holiday.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.jefrajames.choreodemo.holiday.events.BookHolidayFailedEvent;
import io.jefrajames.choreodemo.holiday.events.HolidayBookCreatedEvent;
import io.jefrajames.choreodemo.holiday.events.HolidayBookSagaFinishedEvent;
import io.jefrajames.choreodemo.trip.events.TransportType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.ToString;

@Entity
@Cacheable
@Table(name="choreo_holiday")
@ToString(includeFieldNames = true)
@JsonPropertyOrder({"id", "booked_at", "status", "business_error", "book_response_time", "customer_id", "departure", "destination", "departure_date", "return_date", "category", "people_count", "trip_id", "departure_time", "return_time", "transport_type", "pricing"})
public class Holiday extends PanacheEntityBase {

    @Id
    public String id;

    @JsonProperty("booked_at")
    @Column(name="booked_at", updatable = false)
    public LocalDateTime bookedAt;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    public HolidayStatus status;

    @Column(name="business_error")
    @JsonProperty("business_error")
    public String businessError;

    @Column(name="customer_id", updatable = false)
    @JsonProperty("customer_id")
    public Long customerId;
    
    @Column(name="departure", updatable = false)
    public String departure;

    @Column(name="destination", updatable = false)
    public String destination;

    @Column(name = "departure_date", updatable = false)
    @JsonProperty("departure_date")
    public LocalDate departureDate;

    @Column(name = "return_date", updatable = false)
    @JsonProperty("return_date")
    public LocalDate returnDate;

    @Column(name= "category", updatable = false)
    @Enumerated(EnumType.STRING)
    public HolidayCategory category;

    @Column(name= "people_count", updatable = false)
    @JsonProperty("people_count")
    public int peopleCount;

    @Column(name="trip_id")
    @JsonProperty("trip_id")
    public Long tripId;

    @Column(name = "departure_time")
    @JsonProperty("departure_time")
    public LocalTime departureTime;

    @Column(name = "return_time")
    @JsonProperty("return_time")
    public LocalTime returnTime;

    @Column(name="transport_type")
    @JsonProperty("transport_type")
    public TransportType transportType;

    @Column(name="pricing", precision=10, scale = 2)
    public BigDecimal pricing;

    public HolidayBookCreatedEvent toHolidayBookCreatedEvent() {

        HolidayBookCreatedEvent evt = new HolidayBookCreatedEvent();
    
        evt.setHolidayId(this.id);
        evt.setCustomerId(this.customerId);
        evt.setDeparture(this.departure);
        evt.setDepartureDate(this.departureDate);
        evt.setReturnDate(this.returnDate);
        evt.setDestination(this.destination);
        evt.setSeatCount(this.peopleCount);
        evt.setFirstClass(this.category == HolidayCategory.LUXURY);
    
        return evt;
      }

      
      public HolidayBookSagaFinishedEvent toHolidayBookProcessedEvent() {
        HolidayBookSagaFinishedEvent evt = new HolidayBookSagaFinishedEvent();
        evt.setHolidayId(this.id);
        return evt;
      }
      

      public BookHolidayFailedEvent toHolidayBookFailedEvent() {
        BookHolidayFailedEvent evt = new BookHolidayFailedEvent();
        evt.setBusinessError(this.businessError);
        evt.setHolidayId(this.id);
        evt.setTripId(this.tripId);
        return evt;
      }

}
