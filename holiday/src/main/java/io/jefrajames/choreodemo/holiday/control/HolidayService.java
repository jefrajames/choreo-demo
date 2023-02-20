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

package io.jefrajames.choreodemo.holiday.control;

import java.math.BigDecimal;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.jefrajames.choreodemo.holiday.entity.Holiday;
import io.jefrajames.choreodemo.holiday.entity.HolidayStatus;
import io.jefrajames.choreodemo.holiday.events.HolidayBookCreatedEvent;
import io.jefrajames.choreodemo.holiday.events.HolidayBookSagaFinishedEvent;
import io.jefrajames.choreodemo.trip.events.BookTripFailedEvent;
import io.jefrajames.choreodemo.trip.events.TripBookedEvent;
import lombok.extern.java.Log;

/**
 * This class implements business logic called by the SAGA
 * 
 */
@ApplicationScoped
@Log
public class HolidayService {

    private static final BigDecimal MAX_PRICING = new BigDecimal(500.00);
    private static final long CUSTOMER_ID = 42;

    @Inject
    DomainEventPublisher domainEventPublisher;

    public void checkCustomer(Holiday holiday) {
        if (holiday.customerId != CUSTOMER_ID) {
            holiday.businessError = "Unknwown customer";
            holiday.status = HolidayStatus.REJECTED;
        }
    }

    public void checkPricing(Holiday holiday) {
        if (holiday.pricing != null && holiday.pricing.compareTo(MAX_PRICING) == 1) {
            holiday.businessError = "Max pricing exceeded";
            holiday.status=HolidayStatus.REJECTED;
        }
    }
    
    @Transactional
    public void createHoliday(Holiday holiday) {
        log.fine("createHoliday called with " + holiday);

        
        checkCustomer(holiday);
        if ( holiday.status == HolidayStatus.REJECTED ) {
            Holiday.persist(holiday);
            HolidayBookSagaFinishedEvent bookProcessedEvent = holiday.toHolidayBookProcessedEvent();
            domainEventPublisher.publish(Holiday.class, holiday.id, Collections.singletonList(bookProcessedEvent));
            return;
        }

        Holiday.persist(holiday);

        HolidayBookCreatedEvent evt = holiday.toHolidayBookCreatedEvent();

        domainEventPublisher.publish(Holiday.class, holiday.id,
                Collections.singletonList(evt));
    }

    private void completeWithTripBookedEvent(Holiday holiday, TripBookedEvent evt) {
        holiday.status = HolidayStatus.ACCEPTED;
        holiday.departureTime = evt.getDepartureTime();
        holiday.pricing=evt.getPricing();
        holiday.returnTime=evt.getReturnTime();
        holiday.transportType=evt.getTransportType();
        holiday.tripId=evt.getTripId();
    }

    @Transactional
    public void tripBooked(TripBookedEvent evt) {

        Holiday holiday = Holiday.findById(evt.getHolidayId());
        if (holiday == null) {
            log.warning("tripBooked NOK, holiday not found with id" + evt.getHolidayId());
            return;
        }

        completeWithTripBookedEvent(holiday, evt);

        checkPricing(holiday);

        if ( holiday.status == HolidayStatus.REJECTED ) {
            domainEventPublisher.publish(Holiday.class, holiday.id,
                    Collections.singletonList(holiday.toHolidayBookFailedEvent()));
        }


        HolidayBookSagaFinishedEvent bookProcessedEvent = holiday.toHolidayBookProcessedEvent();
        domainEventPublisher.publish(Holiday.class, holiday.id, Collections.singletonList(bookProcessedEvent));

        return;
    }

    private void completeWithBookTripFailedEvent(Holiday holiday, BookTripFailedEvent evt) {
        holiday.status = HolidayStatus.REJECTED;
        holiday.businessError = evt.getBusinessError();
    }

    @Transactional
    public void bookTripFailed(BookTripFailedEvent evt) {

        Holiday holiday = Holiday.findById(evt.getHolidayId());
        if (holiday == null) {
            log.warning("tripBooked NOK, holiday not found with id" + evt.getHolidayId());
            return;
        }

        completeWithBookTripFailedEvent(holiday, evt);

        HolidayBookSagaFinishedEvent finishedEvent = holiday.toHolidayBookProcessedEvent();
        domainEventPublisher.publish(Holiday.class, holiday.id, Collections.singletonList(finishedEvent));
        return;
    }

}