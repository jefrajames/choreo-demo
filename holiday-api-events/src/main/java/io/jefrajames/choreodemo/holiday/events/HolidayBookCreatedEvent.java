package io.jefrajames.choreodemo.holiday.events;

import java.time.LocalDate;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HolidayBookCreatedEvent implements DomainEvent {

    private String holidayId;

    private Long customerId;
    private String departure;
    private String destination;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private Integer seatCount;
    private Boolean firstClass;
    
}
