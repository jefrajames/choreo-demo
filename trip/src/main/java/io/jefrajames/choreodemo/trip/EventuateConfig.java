package io.jefrajames.choreodemo.trip;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.jefrajames.choreodemo.trip.control.HolidayEventConsumer;
import io.quarkus.runtime.StartupEvent;

/**
 * This class declares the command handlers for a SAGA.
 * 
 */
@Singleton
public class EventuateConfig {

  @ConfigProperty(name = "app.eventuate.dispatcher.id")
  String dispatcherId;

  @Produces
  public DomainEventDispatcher domainEventDispatcher(HolidayEventConsumer holidayEventConsumer,
      DomainEventDispatcherFactory domainEventDispatcherFactory) {
    return domainEventDispatcherFactory.make(dispatcherId, holidayEventConsumer.domainEventHandlers());
  }

  // Jackson config for DateTime
  public void addJavaTimeModule(@Observes StartupEvent ev) {
    ObjectMapper om = io.eventuate.common.json.mapper.JSonMapper.objectMapper;
    om.registerModule(new JavaTimeModule());
  }

}
