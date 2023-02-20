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

package io.jefrajames.choreodemo.trip.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.jefrajames.choreodemo.trip.events.BookTripFailedEvent;
import io.jefrajames.choreodemo.trip.events.TransportType;
import io.jefrajames.choreodemo.trip.events.TripBookedEvent;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@NoArgsConstructor
public class Trip extends PanacheEntity {

    @Column(name = "customer_id")
    @JsonProperty("customer_id")
    public Long customerId;

    @JsonProperty("booked_at")
    @Column(name="booked_at")
    public LocalDateTime bookedAt;

    @Column(name="departure")
    public String departure;

    @Column(name="destination")
    public String destination;

    @JsonProperty("seat_count")
    @Column(name="seat_count")
    public Integer seatCount;

    @JsonProperty("departure_date")
    @Column(name="departure_date")
    public LocalDate departureDate;

    @JsonProperty("departure_time")
    @Column(name="departure_time")
    public LocalTime departureTime;

    @JsonProperty("return_date")
    @Column(name="return_date")
    public LocalDate returnDate;

    @JsonProperty("return_time")
    @Column(name="return_time")
    public LocalTime returnTime;

    @JsonProperty("first_class")
    @Column(name="first_class")
    public Boolean firstClass;

    @Column(name="pricing", precision=10, scale = 2)
    public BigDecimal pricing;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    public TripStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="transport_type")
    @JsonProperty("transport_type")
    public TransportType transportType;

    @Column(name="business_error")
    @JsonProperty("business_error")
    public String businessError;

    @Column(name="holiday_id")
    @JsonProperty("holiday_id")
    public String holidayId;

    public TripBookedEvent toTripBookedEvent() {

        TripBookedEvent evt = new TripBookedEvent();

        evt.setDepartureTime(this.departureTime);
        evt.setPricing(this.pricing);
        evt.setReturnTime(this.returnTime);
        evt.setTransportType(this.transportType);
        evt.setHolidayId(this.holidayId);
        evt.setTripId(this.id);

        return evt;
    }

    public BookTripFailedEvent toBookTripFailedEvent() {
        BookTripFailedEvent evt = new BookTripFailedEvent();

        evt.setBusinessError(this.businessError);
        evt.setTripId(this.id);
        evt.setHolidayId(this.holidayId);

        return evt;
    }

}
