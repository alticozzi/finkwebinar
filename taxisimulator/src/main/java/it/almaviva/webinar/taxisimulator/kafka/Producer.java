package it.almaviva.webinar.taxisimulator.kafka;


import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.almaviva.webinar.taxisimulator.model.taxi.TaxiPOJO;

public class Producer {

    private static final Logger logger = Logger.getLogger(Producer.class);
    private String topic = null;
    private KafkaProducer<String, String> producerKafka;
    private static final ObjectMapper mapper = new ObjectMapper();
    

    public Producer(String bootstrapservers, String topic) {
        Properties properties = new Properties();
        this.topic = topic;
        properties.setProperty("bootstrap.servers", bootstrapservers);
        properties.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        producerKafka = new KafkaProducer<>(properties);
    }

    public void publish(TaxiPOJO taxiPOJO) {
        String strTaxi;
        try {
            strTaxi = mapper.writeValueAsString(taxiPOJO);
        } catch (JsonProcessingException e) { return; }
        producerKafka.send(new ProducerRecord<String, String>(topic, "", strTaxi));
        logger.info("Message Taxi! Topic: " + topic + " Value: " + strTaxi);
    }
}
