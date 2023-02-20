package io.jefrajames.choreodemo.holiday.events;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookHolidayFailedEvent implements DomainEvent {

    private String holidayId;
    private Long tripId;
    private String businessError;
    
}
