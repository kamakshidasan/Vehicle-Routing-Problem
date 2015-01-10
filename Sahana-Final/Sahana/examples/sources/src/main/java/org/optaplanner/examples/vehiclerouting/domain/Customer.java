/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.examples.vehiclerouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.vehiclerouting.domain.solver.VehicleUpdatingVariableListener;
import org.optaplanner.examples.vehiclerouting.domain.solver.VrpCustomerDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = VrpCustomerDifficultyComparator.class)
@XStreamAlias("customer")

public class Customer extends AbstractPersistable implements Standstill {

    protected Location location;
    protected Item item;
    protected int quantity;

    // Planning variables: changes during planning, between score calculations.
    protected Standstill previousStandstill;

    // Shadow variables
    protected Customer nextCustomer;
    protected Vehicle vehicle;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public Item getItem(){
        return item;
    }

    public double getCustomerItemWeight(){
        return getItem().getItemWeight();
    }

    public double getCustomerItemVolume(){
        return getItem().getItemVolume();
    }

    public double getWeightDemand(){
        return getCustomerItemWeight()*getQuantity();
    }

    public double getVolumeDemand(){
        return getCustomerItemVolume()*getQuantity();
    }

    @PlanningVariable(valueRangeProviderRefs = {"vehicleRange", "customerRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(Standstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    public Customer getNextCustomer() {
        return nextCustomer;
    }

    public void setNextCustomer(Customer nextCustomer) {
        this.nextCustomer = nextCustomer;
    }

    @CustomShadowVariable(variableListenerClass = VehicleUpdatingVariableListener.class,
            sources = {@CustomShadowVariable.Source(variableName = "previousStandstill")})
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public int getDistanceToPreviousStandstill() {
        if (previousStandstill == null) {
            return 0;
        }
        return getDistanceTo(previousStandstill);
    }

    public int getDistanceTo(Standstill standstill) {
        return location.getDistance(standstill.getLocation());
    }


    @Override
    public String toString() {
        return location + "(after " + (previousStandstill == null ? "null" : previousStandstill.getLocation()) + ")";
    }

}
