/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.micronaut

import example.micronaut.processor.TestClass
import example.micronaut.processor.TestService
import example.micronaut.processor.ViewedPetsProcessor
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Status
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Path("/pets") // <1>
class PetResource(val petRepository: PetRepository,val testService:TestService) { // <2>

    @GET // <3>
    fun all():List<Pet>  {
        val pets = petRepository.findAll()

        testService.testMethod(pets)
        TestClass().testMethod(pets)

//        GlobalScope.launch(Context.current().asContextElement()) {
        GlobalScope.launch() {
            ViewedPetsProcessor().processPets(flow {
                pets.forEach {
                    delay(1000)
                    emit(it)
                }
            })
        }

        return pets

    } // <4>

    @GET // <3>
    @Path("/{name}") // <4>
    fun byName(@PathParam("name") petsName: String) = petRepository.findByName(petsName) // <5>

    @POST
    @Status(HttpStatus.CREATED)
    fun save(@Body petSave: PetSave) {
        petRepository.save(petSave.name, petSave.type)
    }
}