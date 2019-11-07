package it.almaviva.webinar.flink.taxianalyzer.model.districtinfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistrictInfo {

    @JsonProperty("district")
    private String district = null;
    @JsonProperty("numFreeTaxi")
    private int numFreeTaxi = 0;
    @JsonProperty("numBusyTaxi")
    private int numBusyTaxi = 0;
    
    public DistrictInfo() {}
    
    public DistrictInfo(String district, int numFreeTaxi, int numBusyTaxi) {
        super();
        this.district = district;
        this.numFreeTaxi = numFreeTaxi;
        this.numBusyTaxi = numBusyTaxi;
    }

    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }
    public int getNumFreeTaxi() {
        return numFreeTaxi;
    }
    public void setNumFreeTaxi(int numFreeTaxi) {
        this.numFreeTaxi = numFreeTaxi;
    }
    public int getNumBusyTaxi() {
        return numBusyTaxi;
    }
    public void setNumBusyTaxi(int numBusyTaxi) {
        this.numBusyTaxi = numBusyTaxi;
    }

    @Override
    public String toString() {
        return "DistrictInfo [district=" + district + ", numFreeTaxi=" + numFreeTaxi
                + ", numBusyTaxi=" + numBusyTaxi + "]";
    }
    
}