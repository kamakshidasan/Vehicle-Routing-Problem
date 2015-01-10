/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.vehiclerouting.solver.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleRoutingEasyScoreCalculator implements EasyScoreCalculator<VehicleRoutingSolution> {

    public HardSoftScore calculateScore(VehicleRoutingSolution schedule) {
        List<Customer> customerList = schedule.getCustomerList();
        List<Vehicle> vehicleList = schedule.getVehicleList();
        Map<Vehicle, Double> vehicleWeightMap = new HashMap<Vehicle, Double>(vehicleList.size());
        Map<Vehicle, Double> vehicleVolumeMap = new HashMap<Vehicle, Double>(vehicleList.size());
        for (Vehicle vehicle : vehicleList) {
            vehicleWeightMap.put(vehicle, 0.0);
        }
        for (Vehicle vehicle : vehicleList) {
            vehicleVolumeMap.put(vehicle, 0.0);
        }
        int hardScore = 0;
        int softScore = 0;
        for (Customer customer : customerList) {
            Standstill previousStandstill = customer.getPreviousStandstill();
            if (previousStandstill != null) {
                Vehicle vehicle = customer.getVehicle();
                vehicleWeightMap.put(vehicle, vehicleWeightMap.get(vehicle) + customer.getWeightDemand());
                vehicleVolumeMap.put(vehicle, vehicleVolumeMap.get(vehicle) + customer.getVolumeDemand());
                softScore -= customer.getDistanceToPreviousStandstill();
                if (customer.getNextCustomer() == null) {
                    softScore -= vehicle.getLocation().getDistance(customer.getLocation());
                }
            }
        }
        for (Map.Entry<Vehicle, Double> entry : vehicleWeightMap.entrySet()) {
            Vehicle vehicle = entry.getKey();
            double weightCapacity = vehicle.getWeightCapacity();
            double weightDemand = entry.getValue();
            double volumeCapacity = vehicle.getVolumeCapacity();
            double volumeDemand = vehicleVolumeMap.get(vehicle);
            if (weightDemand > weightCapacity && volumeDemand > volumeCapacity) {
                hardScore -= (weightDemand - weightCapacity);
            }

        }
        return HardSoftScore.valueOf(hardScore, softScore);
    }

}
