package it.almaviva.webinar.flink.taxianalyzer.elaboration;

import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import it.almaviva.webinar.flink.taxianalyzer.model.taxi.TaxiPosition;

public class FilterCity extends RichFilterFunction<TaxiPosition> {

    private static final long serialVersionUID = 1L;
    private transient String city;
    
    @Override
    public void open(Configuration configuration) throws Exception {
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        city = parameterTool.get("city");
    }
    
    @Override
    public boolean filter(TaxiPosition taxiPosition) throws Exception {
        return taxiPosition.getCity().equals(city);
    }

}