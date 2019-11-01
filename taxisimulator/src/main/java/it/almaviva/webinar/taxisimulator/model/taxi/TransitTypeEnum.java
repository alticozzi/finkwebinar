package it.almaviva.webinar.taxisimulator.model.taxi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransitTypeEnum {
    
    BUSY("BUSY"),
    FREE("FREE"),
    LEAVE("LEAVE");
    
    private String value;

    TransitTypeEnum(String value) {
      this.value = value;
    }
    
    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TransitTypeEnum fromValue(String text) {
      for (TransitTypeEnum b : TransitTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
}