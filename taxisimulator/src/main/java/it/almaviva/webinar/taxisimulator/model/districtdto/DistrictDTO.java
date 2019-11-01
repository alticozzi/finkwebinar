package it.almaviva.webinar.taxisimulator.model.districtdto;

public class DistrictDTO {

    private String name = null;
    private Integer priority = null;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        return String.format("DistrictDTO [name=%s, priority=%s]", name, priority);
    }
}