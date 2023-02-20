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
package io.jefrajames.choreodemo.holiday;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.jefrajames.choreodemo.holiday.boundary.HolidayBookResponder;
import io.jefrajames.choreodemo.holiday.control.TripEventConsumer;
import io.quarkus.runtime.StartupEvent;

@Singleton
public class EventuateConfig {

  @ConfigProperty(name = "app.eventuate.dispatcher.id")
  String dispatcherId;

  @Produces
  public DomainEventDispatcher domainEventDispatcher(HolidayBookResponder holidayEventConsumer,
      DomainEventDispatcherFactory domainEventDispatcherFactory) {
    return domainEventDispatcherFactory.make(dispatcherId, holidayEventConsumer.domainEventHandlers());
  }

  @Produces
  public DomainEventDispatcher domainEventDispatcher(TripEventConsumer tripEventConsumer,
      DomainEventDispatcherFactory domainEventDispatcherFactory) {
    return domainEventDispatcherFactory.make(dispatcherId, tripEventConsumer.domainEventHandlers());
  }

  // Jackson configuration to properly manage Java time
  public void addJavaTimeModule(@Observes StartupEvent ev) {
    ObjectMapper om = io.eventuate.common.json.mapper.JSonMapper.objectMapper;
    om.registerModule(new JavaTimeModule());
  }

}