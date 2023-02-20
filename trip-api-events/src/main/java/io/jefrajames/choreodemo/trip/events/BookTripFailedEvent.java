package io.jefrajames.choreodemo.trip.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookTripFailedEvent implements DomainEvent {
    private Long tripId;
    private String businessError;
    private String holidayId;
}
