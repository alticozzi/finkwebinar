package it.almaviva.webinar.flink.taxianalyzer.elaboration;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.almaviva.webinar.flink.taxianalyzer.deserialization.DeserializationTaxiPosition;
import it.almaviva.webinar.flink.taxianalyzer.model.districtinfo.DistrictInfo;

public class MapJsonDistrictInfo implements MapFunction<DistrictInfo, String> {

    private static final long serialVersionUID = 1L;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(DeserializationTaxiPosition.class);

    @Override
    public String map(DistrictInfo districtInfo) throws Exception {
        String writeValueAsString = mapper.writeValueAsString(districtInfo);
        logger.info(writeValueAsString);
        return writeValueAsString;
    }
}
