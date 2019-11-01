package it.almaviva.webinar.flink.taxianalyzer.deserialization;

import java.io.IOException;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.almaviva.webinar.flink.taxianalyzer.model.taxi.TaxiPosition;

public class DeserializationTaxiPosition extends AbstractDeserializationSchema<TaxiPosition> {

    private static final long serialVersionUID = 1L;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public TaxiPosition deserialize(byte[] message) throws IOException {
        TaxiPosition taxiPosition = null;
        try {
            taxiPosition = mapper.readValue(message, TaxiPosition.class);
        } catch (Exception e) {
            System.out.println("Errore nella deserializzazione da Kafka. Message " + e.getMessage());
        }
        return taxiPosition;
    }

    @Override
    public boolean isEndOfStream(TaxiPosition nextElement) {
        return false;
    }
}