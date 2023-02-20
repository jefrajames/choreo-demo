package io.jefrajames.choreodemo.holiday.control;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import io.jefrajames.choreodemo.trip.events.BookTripFailedEvent;
import io.jefrajames.choreodemo.trip.events.TripBookedEvent;
import lombok.extern.java.Log;

@Log
@ApplicationScoped
public class TripEventConsumer {

    @ConfigProperty(name = "app.eventuate.trip.events.aggregate.type")
    String tripAggregateType;

    @Inject
    HolidayService holidayService;

    public DomainEventHandlers domainEventHandlers() {

        return DomainEventHandlersBuilder
                .forAggregateType(tripAggregateType)
                .onEvent(TripBookedEvent.class, this::onTripBooked)
                .onEvent(BookTripFailedEvent.class, this::onBookTripFailed)
                .build();
    }

    public void onTripBooked(DomainEventEnvelope<TripBookedEvent> de) {
        log.info("Received event " + de);
        holidayService.tripBooked(de.getEvent());
    }

    public void onBookTripFailed(DomainEventEnvelope<BookTripFailedEvent> de) {
        log.info("Received event " + de);
        holidayService.bookTripFailed(de.getEvent());
    }

}
