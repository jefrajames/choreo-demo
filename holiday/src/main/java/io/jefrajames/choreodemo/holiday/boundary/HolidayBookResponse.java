// Copyright 2023 jefrajames
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

import java.util.concurrent.CompletableFuture;

import javax.ws.rs.core.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
/**
 * This class contains data needed to generate a JAX-RS 
 * response in an asynchronous way.
 */
public class HolidayBookResponse {

    private String holidayId;
    private String resourceUri;
    private CompletableFuture<Response> response;
    
}
