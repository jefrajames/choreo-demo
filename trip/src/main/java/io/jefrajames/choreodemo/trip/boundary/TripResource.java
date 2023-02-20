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

package io.jefrajames.choreodemo.trip.boundary;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.jefrajames.choreodemo.trip.control.TripService;
import io.jefrajames.choreodemo.trip.entity.Trip;

@RequestScoped
@Path("/trips")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TripResource {

    @Inject
    TripService tripService;
    
    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "200", description = "Trip found", content = @Content(mediaType = "application/json"))
    @APIResponse(responseCode = "204", description = "Trip not found")
    @Operation(description = "Find a trip by id", summary = "Find by id")
    public Trip findById(@Parameter(name = "id", description = "Trip identifier") @PathParam("id") @NotNull Long id) {

        Trip trip = Trip.findById(id);

        if ( trip==null ) {
            Response rep = Response.status(Response.Status.NOT_FOUND).entity("{\"Trip not found\"" + ":" + id + "}").build();
            throw new WebApplicationException(rep);
        }

        return trip;
    }

    @GET
    public List<Trip> findAll() {
        return Trip.listAll(null);
    }
    
}