package it.almaviva.webinar.taxisimulator.model.districtdto;

import java.util.List;

public class DistrictConf {

    private Integer numTaxi = null;
    private List<DistrictDTO> districts = null;
    
    public Integer getNumTaxi() {
        return numTaxi;
    }
    public void setNumTaxi(Integer numTaxi) {
        this.numTaxi = numTaxi;
    }
    public List<DistrictDTO> getDistricts() {
        return districts;
    }
    public void setDistricts(List<DistrictDTO> districts) {
        this.districts = districts;
    }
    
    
}
