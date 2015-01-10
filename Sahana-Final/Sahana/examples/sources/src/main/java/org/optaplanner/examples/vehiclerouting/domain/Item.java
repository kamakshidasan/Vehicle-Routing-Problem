package org.optaplanner.examples.vehiclerouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;

/**
 * Created by Aishwarya on 1/10/2015.
 */

@XStreamAlias("item")
public class Item extends AbstractPersistable{

    protected String name;
    protected double itemWeight;
    protected double itemVolume;

    public String getName() {
        return name;
    }

    public double getItemWeight() {
        return itemWeight;
    }

    public double getItemVolume() {
        return itemVolume;
    }
}
