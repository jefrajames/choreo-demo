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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.jefrajames.choreodemo.holiday.control.HolidayService;
import io.jefrajames.choreodemo.holiday.entity.Holiday;
import lombok.extern.java.Log;

@Log
@ApplicationScoped
@Path("/holidays")
@ResponseTimed
public class HolidayResource {

    @Inject
    HolidayService holidayService;

    @Inject
    HolidayBookResponder responder;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(description = "Find all holidays", summary = "Find all holidays")
    @APIResponse(responseCode = "200", description = "Holiday list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Holiday.class)))
    public List<Holiday> findAll() {
        return Holiday.findAll().list();
    }

    @GET
    @Path("/{id}")
    @Operation(description = "Find a holiday by id", summary = "Find by id")
    @APIResponse(responseCode = "200", description = "Holiday found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Holiday.class)))
    @APIResponse(responseCode = "404", description = "Holiday not found")
    public Holiday findById(@PathParam("id") String id) {

        Holiday holiday = Holiday.findById(id);

        if (holiday == null) {
            Response rep = Response.status(Response.Status.NOT_FOUND).entity("{\"Holiday not found\"" + ":" + id + "}")
                    .build();
            throw new WebApplicationException(rep);
        }

        return holiday;
    }

    /**
     * Process a HolidayBookRequest.
     * 
     * A CompletionStage is returned since the processing is asynchronous.
     * 
     * @param bookRequest
     * @return
     */
    @POST
    @Operation(description = "Holiday booking", summary = "Book a holiday")
    @RequestBody(name = "holiday", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HolidayBookRequest.class), examples = @ExampleObject(name = "Let's go to London", value = "{\"customer_id\": 42, \"departure\": \"Paris\", \"destination\": \"London\", \"departure_date\": \"2023-04-23\", \"return_date\": \"2023-04-26\", \"category\": \"BASIC\", \"people_count\": 1 }")), required = true, description = "example of a book holiday")
    @APIResponse(responseCode = "201", description = "Holiday booked")
    @APIResponse(responseCode = "422", description = "Error holiday not booked")
    public CompletionStage<Response> book(@Valid HolidayBookRequest bookRequest) {

        Holiday holiday = bookRequest.toEntity();

        CompletableFuture<Response> futureResponse = new CompletableFuture<>();
        
        HolidayBookResponse response = new HolidayBookResponse(holiday.id,
                uriInfo.getAbsolutePath().getPath(), futureResponse);
        responder.addPendingResponse(holiday.id, response);

        holidayService.createHoliday(holiday);

        return futureResponse;
    }

}