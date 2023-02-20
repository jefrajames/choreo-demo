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

package io.jefrajames.choreodemo.holiday.boundary;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import io.jefrajames.choreodemo.holiday.entity.Holiday;
import io.jefrajames.choreodemo.holiday.entity.HolidayStatus;
import io.jefrajames.choreodemo.holiday.events.HolidayBookSagaFinishedEvent;
import lombok.extern.java.Log;

/**
 * This class is in charge to complete the JAX-RS response when
 * the processing is done in reaction to HolidayBookProcessedEvent.
 * 
 */
@Log
@ApplicationScoped
public class HolidayBookResponder {

  private Map<String, HolidayBookResponse> pendindResponses = new ConcurrentHashMap<>();

  public void addPendingResponse(String holidayId, HolidayBookResponse pendingResponse) {
    pendindResponses.put(holidayId, pendingResponse);
  }

  private HolidayBookResponse getPendingResponse(String holidayId) {

    HolidayBookResponse response = pendindResponses.get(holidayId);

    if (response != null)
      pendindResponses.remove(holidayId);

    return response;
  }

  // Event handlers declaration, called by EventuateConfig
  public DomainEventHandlers domainEventHandlers() {

    return DomainEventHandlersBuilder
        .forAggregateType(Holiday.class.getName())
        .onEvent(HolidayBookSagaFinishedEvent.class, this::onBookSagaFinished)
        .build();
  }

  private URI resourceUri(String holidayId, String requestedURI) {
    return URI.create(String.format("%s/%s", requestedURI, holidayId));
  }

  public void onBookSagaFinished(DomainEventEnvelope<HolidayBookSagaFinishedEvent> de) {

    HolidayBookSagaFinishedEvent evt = de.getEvent();

    HolidayBookResponse pendingResponse = getPendingResponse(evt.getHolidayId());
    if ( pendingResponse==null ) {
      log.warning("No pendingResponse found for holiday id " + evt.getHolidayId());
      return;
    }

    Holiday holiday = Holiday.findById(evt.getHolidayId());

    if (holiday.status == HolidayStatus.ACCEPTED) {
      URI uri = resourceUri(evt.getHolidayId(), pendingResponse.getResourceUri());
      pendingResponse.getResponse().complete(Response.created(uri).entity(holiday).build());
    } else
      pendingResponse.getResponse().complete(Response.status(422).entity(holiday).build());
  }

}
