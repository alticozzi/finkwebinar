package it.almaviva.webinar.taxisimulator.model.taxi;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaxiPOJO {

    @JsonProperty("id")
    private String id = null;
    @JsonProperty("city")
    private String city = null;
    @JsonProperty("district")
    private String district = null;
    @JsonProperty("transitType")
    private TransitTypeEnum transitType = null;
    @JsonProperty("timestamp")
    private Date timestamp = null;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }
    public TransitTypeEnum getTransitType() {
        return transitType;
    }
    public void setTransitType(TransitTypeEnum transitType) {
        this.transitType = transitType;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
