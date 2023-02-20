package io.jefrajames.choreodemo.trip.control;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import io.jefrajames.choreodemo.holiday.events.BookHolidayFailedEvent;
import io.jefrajames.choreodemo.holiday.events.HolidayBookCreatedEvent;
import io.jefrajames.choreodemo.trip.entity.Trip;
import io.jefrajames.choreodemo.trip.entity.TripStatus;
import lombok.extern.java.Log;

@Log
@Singleton
public class HolidayEventConsumer {

  @ConfigProperty(name = "app.eventuate.holiday.events.aggregate.type")
  String aggregateType;

  @Inject
  TripService tripService;

  private Trip event2Entity(HolidayBookCreatedEvent evt, String holidayId) {
    Trip trip = new Trip();

    trip.holidayId = holidayId;
    trip.bookedAt = LocalDateTime.now();
    trip.status = TripStatus.PENDING;
    trip.customerId = evt.getCustomerId();
    trip.departure = evt.getDeparture();
    trip.destination = evt.getDestination();
    trip.departureDate = evt.getDepartureDate();
    trip.firstClass = evt.getFirstClass();
    trip.returnDate = evt.getReturnDate();
    trip.seatCount = evt.getSeatCount();
    

    return trip;
  }

  // Called by EventuateConfig
  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
        .forAggregateType(aggregateType)
        .onEvent(HolidayBookCreatedEvent.class, this::onHolidayBookCreated)
        .onEvent(BookHolidayFailedEvent.class, this::onHolidayBookFailedEvent)
        .build();
  }

  @Transactional
  public void onHolidayBookCreated(DomainEventEnvelope<HolidayBookCreatedEvent> de) {
    log.info("Got HolidayBookCreatedEvent " + de);
    Trip trip = event2Entity(de .getEvent(), de.getAggregateId());
    tripService.book(trip);
  }

  @Transactional
  public void onHolidayBookFailedEvent(DomainEventEnvelope<BookHolidayFailedEvent> de) {
    log.info("Got BookHolidayFailedEvent " + de);
    tripService.cancel(de.getEvent());
  }

}
