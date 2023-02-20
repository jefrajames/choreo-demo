package io.jefrajames.choreodemo.trip.events;

import java.math.BigDecimal;
import java.time.LocalTime;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TripBookedEvent implements DomainEvent {

    private String holidayId;

    private Long tripId;
    private TransportType transportType;
    private LocalTime departureTime;
    private LocalTime returnTime;
    private BigDecimal pricing;

}