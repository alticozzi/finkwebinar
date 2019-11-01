package it.almaviva.webinar.flink.taxianalyzer.elaboration;

import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import it.almaviva.webinar.flink.taxianalyzer.model.districtinfo.DistrictInfo;
import it.almaviva.webinar.flink.taxianalyzer.model.taxi.TaxiPosition;

public class FlatMapDistrictInfo extends RichFlatMapFunction<TaxiPosition, DistrictInfo> {

    private static final long serialVersionUID = 1L;

    private transient ValueState<List<String>> freeTaxis = null;
    private transient ValueState<List<String>> busyTaxis = null;
    
    @Override
    public void open(Configuration configuration) throws Exception {
        ValueStateDescriptor<List<String>> descriptorFreeTaxi = new ValueStateDescriptor<>("freeTaxis", TypeInformation.of(new TypeHint<List<String>>() {}));
        ValueStateDescriptor<List<String>> descriptorBusyTaxi = new ValueStateDescriptor<>("busyTaxis", TypeInformation.of(new TypeHint<List<String>>() {}));
        freeTaxis = getRuntimeContext().getState(descriptorFreeTaxi);
        busyTaxis = getRuntimeContext().getState(descriptorBusyTaxi);
    }
    
    @Override
    public void flatMap(TaxiPosition taxiPosition, Collector<DistrictInfo> out) throws Exception {
        List<String> currentFreeTaxi = freeTaxis.value();
        List<String> currentBusyTaxi = busyTaxis.value();
        
        // Inizializzione
        if(currentFreeTaxi == null) {
            currentFreeTaxi = new ArrayList<>();
            freeTaxis.update(currentBusyTaxi);
        } 
        if(currentBusyTaxi == null) {
            currentBusyTaxi = new ArrayList<>();
            busyTaxis.update(currentBusyTaxi);
        } 
        
        int numCurrentFreeTaxi = currentFreeTaxi.size();
        int numCurrentBusyTaxi = currentBusyTaxi.size();
        
        // Business logic
        switch(taxiPosition.getTransitType()) {
            case BUSY:
                add(taxiPosition, currentBusyTaxi);
                remove(taxiPosition, currentFreeTaxi);
                break;
            case FREE:
                add(taxiPosition, currentFreeTaxi);
                remove(taxiPosition, currentBusyTaxi);
                break;
            case LEAVE:
                remove(taxiPosition, currentBusyTaxi);
                remove(taxiPosition, currentFreeTaxi);
                break;
        }
        busyTaxis.update(currentBusyTaxi);
        freeTaxis.update(currentFreeTaxi);
        // Controllo Output
        if(currentFreeTaxi.size() != numCurrentFreeTaxi || currentBusyTaxi.size() != numCurrentBusyTaxi) {
            out.collect(new DistrictInfo(taxiPosition.getDistrict(), currentFreeTaxi.size(), currentBusyTaxi.size()));
        } 
    }

    private void add(TaxiPosition taxiPosition, List<String> list) {
        if(!list.contains(taxiPosition.getId())) {
            list.add(taxiPosition.getId());
        }
    }
    
    private void remove(TaxiPosition taxiPosition, List<String> list) {
        if(list.contains(taxiPosition.getId())) {
            list.remove(taxiPosition.getId());
        }
    }
}
