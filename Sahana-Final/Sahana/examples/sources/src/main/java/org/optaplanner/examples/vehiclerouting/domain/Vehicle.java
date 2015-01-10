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
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("vehicle")
public class Vehicle extends AbstractPersistable implements Standstill {

    protected Depot depot;
    protected Item item;
    protected double weightCapacity;
    protected double volumeCapacity;

    protected Customer nextCustomer;

    public double getWeightCapacity() {
        return weightCapacity;
    }

    public double getVolumeCapacity() {
        return volumeCapacity;
    }

    public Customer getNextCustomer() {
        return nextCustomer;
    }

    public void setNextCustomer(Customer nextCustomer) {
        this.nextCustomer = nextCustomer;
    }

    public Vehicle getVehicle() {
        return this;
    }

    public Location getLocation() {
        return depot.getLocation();
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
