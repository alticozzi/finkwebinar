package it.almaviva.webinar.flink.taxianalyzer.elaboration;

import org.apache.flink.api.common.functions.MapFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.almaviva.webinar.flink.taxianalyzer.model.districtinfo.DistrictInfo;

public class MapJsonDistrictInfo implements MapFunction<DistrictInfo, String> {

    private static final long serialVersionUID = 1L;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String map(DistrictInfo districtInfo) throws Exception {
        String writeValueAsString = mapper.writeValueAsString(districtInfo);
        System.out.println(writeValueAsString);
        return writeValueAsString;
    }
}
