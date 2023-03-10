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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.jefrajames.choreodemo.holiday.entity.Holiday;
import io.jefrajames.choreodemo.holiday.entity.HolidayCategory;
import lombok.Data;

@Data
public class HolidayBookRequest {

    @JsonProperty("customer_id")
    @PositiveOrZero
    private Long customerId;

    @NotEmpty
    private String departure;

    @NotEmpty
    private String destination;

    @JsonProperty("departure_date")
    @FutureOrPresent(message="should be future or present")
    private LocalDate departureDate;

    @JsonProperty("return_date")
    @FutureOrPresent(message="should be future or present")
    private LocalDate returnDate;

    private HolidayCategory category;

    @JsonProperty("people_count")
    @Positive(message="should be positive")
    private int peopleCount;

    public Holiday toEntity() {

        Holiday holiday = new Holiday();

        holiday.id =  UUID.randomUUID().toString();
        holiday.bookedAt = LocalDateTime.now();
        holiday.customerId = this.customerId;
        holiday.destination = this.destination;
        holiday.departure = this.departure;
        holiday.category = this.category;
        holiday.departureDate = this.departureDate;
        holiday.returnDate = this.returnDate;
        holiday.category = this.category;
        holiday.peopleCount = this.peopleCount;

        return holiday;
    }
}