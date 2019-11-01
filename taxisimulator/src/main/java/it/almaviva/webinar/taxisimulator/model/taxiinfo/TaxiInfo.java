package it.almaviva.webinar.taxisimulator.model.taxiinfo;

import java.time.OffsetDateTime;
import it.almaviva.webinar.taxisimulator.model.taxi.TransitTypeEnum;

public class TaxiInfo {

    private String id;
    private int currentNumTic = 0;
    private int currentPriority = 0;
    private String currentDistrict = null;
    private OffsetDateTime nextTrigger = null;
    private TransitTypeEnum transitType = null;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getCurrentNumTic() {
        return currentNumTic;
    }
    public void setCurrentNumTic(int currentNumTic) {
        this.currentNumTic = currentNumTic;
    }
    public int getCurrentPriority() {
        return currentPriority;
    }
    public void setCurrentPriority(int currentPriority) {
        this.currentPriority = currentPriority;
    }
    public String getCurrentDistrict() {
        return currentDistrict;
    }
    public void setCurrentDistrict(String currentDistrict) {
        this.currentDistrict = currentDistrict;
    }
    public OffsetDateTime getNextTrigger() {
        return nextTrigger;
    }
    public void setNextTrigger(OffsetDateTime nextTrigger) {
        this.nextTrigger = nextTrigger;
    }
    public TransitTypeEnum getTransitType() {
        return transitType;
    }
    public void setTransitType(TransitTypeEnum transitType) {
        this.transitType = transitType;
    }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currentDistrict == null) ? 0 : currentDistrict.hashCode());
        result = prime * result + currentNumTic;
        result = prime * result + currentPriority;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nextTrigger == null) ? 0 : nextTrigger.hashCode());
        result = prime * result + ((transitType == null) ? 0 : transitType.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaxiInfo other = (TaxiInfo) obj;
        if (currentPriority != other.currentPriority)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return String.format("%nTaxiInfo [id=%s, currentNumTic=%s, currentPriority=%s, currentDistrict=%s, nextTrigger=%s, transitType=%s]",
                id, currentNumTic, currentPriority, currentDistrict, nextTrigger, transitType);
    }
}