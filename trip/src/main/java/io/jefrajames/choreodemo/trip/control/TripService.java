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

package io.jefrajames.choreodemo.trip.control;

import java.math.BigDecimal;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.jefrajames.choreodemo.holiday.events.BookHolidayFailedEvent;
import io.jefrajames.choreodemo.trip.entity.Destination;
import io.jefrajames.choreodemo.trip.entity.DestinationDB;
import io.jefrajames.choreodemo.trip.entity.Trip;
import io.jefrajames.choreodemo.trip.entity.TripStatus;
import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class TripService {

    @Inject
    DestinationDB destinationDB;

    @Inject
    DomainEventPublisher domainEventPublisher;

    public Trip findById(Long id) {
        return Trip.findById(id);
    }

    private boolean checkDeparture(String departure) {
        return "Paris".equals(departure);
    }

    private void processBook(Trip trip) {

        if (!checkDeparture(trip.departure)) {
            trip.businessError = "Rejected departure " + trip.departure;
            trip.status=TripStatus.REJECTED;
            return;
        }

        Destination destination = destinationDB.lookup(trip.destination);
        if (destination == null) {
            trip.businessError = "Rejected destination " + trip.destination;
            trip.status=TripStatus.REJECTED;
            return;
        }

        trip.status = TripStatus.ACCEPTED;
        trip.pricing = destination.getPricing().multiply(new BigDecimal(trip.seatCount));
        trip.transportType = destination.getTransportType();
        trip.departureTime = destination.getDepartureTime();
        trip.returnTime = destination.getReturnTime();

    }

    @Transactional
    public Trip book(Trip trip) {

        processBook(trip);

        trip.persist();

        DomainEvent evt = (trip.status == TripStatus.ACCEPTED)?trip.toTripBookedEvent():trip.toBookTripFailedEvent();
        domainEventPublisher.publish(Trip.class, trip.id, Collections.singletonList(evt));

        return trip;
    }

    @Transactional
    public void cancel(BookHolidayFailedEvent evt) {

        Trip trip = Trip.findById(evt.getTripId());
        
        if (trip == null) {
            log.warning("Trip with id cannot be canceled not found  " + evt.getTripId());
            return;
        }

        trip.status = TripStatus.CANCELED;
        trip.businessError = "Holiday error: " + evt.getBusinessError();
    }

}