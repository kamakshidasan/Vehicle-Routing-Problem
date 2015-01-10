package org.optaplanner.examples.vehiclerouting.domain;

import com.thoughtworks.xstream.XStream;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@PlanningSolution
@XStreamAlias("VehicleRouting")

public class VehicleRoutingSolution extends AbstractPersistable implements Solution<HardSoftScore> {

    protected String name;
    protected List<Item> itemList;
    protected List<Location> locationList;
    protected List<Depot> depotList;
    protected List<Vehicle> vehicleList;
    protected List<Customer> customerList;

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
    protected HardSoftScore score;

    public List<Location> getLocationList() {
        return locationList;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "vehicleRange")
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "customerRange")
    public List<Customer> getCustomerList() {
        return customerList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(locationList);
        facts.addAll(depotList);
        // Do not add the planning entity's (customerList) because that will be done automatically
        return facts;
    }

    public void generateUnsolvedXML(String xml){
        File inputFile = new File(xml);
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(VehicleRoutingSolution.class);
        VehicleRoutingSolution vehicleRoutingSolution = (VehicleRoutingSolution)xStream.fromXML(xml);
        File outputFile = null;
        try {
            String outputFileName = "unsolved-"+inputFile.getName();
            outputFile=new File(inputFile.getParent(),outputFileName);
            xStream.toXML(vehicleRoutingSolution, new FileOutputStream(outputFile));
        } catch(Exception exception) {
            LoggerFactory.getLogger(getClass()).info("Exception: {}", exception.getMessage());
        }
    }

    public static void main(String args[]){
        VehicleRoutingSolution vehicleRoutingSolution = new VehicleRoutingSolution();
        vehicleRoutingSolution.generateUnsolvedXML(args[0]);
    }


}
