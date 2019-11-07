package it.almaviva.webinar.flink.taxianalyzer;

import java.util.Properties;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.log4j.Logger;
import it.almaviva.webinar.flink.taxianalyzer.deserialization.DeserializationTaxiPosition;
import it.almaviva.webinar.flink.taxianalyzer.elaboration.FilterCity;
import it.almaviva.webinar.flink.taxianalyzer.elaboration.FlatMapDistrictInfo;
import it.almaviva.webinar.flink.taxianalyzer.elaboration.MapJsonDistrictInfo;
import it.almaviva.webinar.flink.taxianalyzer.model.taxi.TaxiPosition;

public class TaxiAnalyzer {

    private static final Logger logger = Logger.getLogger(TaxiAnalyzer.class);
    
    public static void main(String[] args) throws Exception {

        ParameterTool parameterTool = ParameterTool.fromPropertiesFile(TaxiAnalyzer.class.getClassLoader().getResourceAsStream("flink.properties"));
        logger.info(parameterTool.toMap());
        
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        env.setParallelism(8);
        
        env.setBufferTimeout(100);
        
        env.getConfig().setGlobalJobParameters(parameterTool);
        
        env.getConfig().disableSysoutLogging();
        
        Properties kafkaConsumerProperties = new Properties();
        kafkaConsumerProperties.setProperty("bootstrap.servers",  parameterTool.get("bootstrap.servers"));
        kafkaConsumerProperties.setProperty("group.id", parameterTool.get("group.id"));
        kafkaConsumerProperties.setProperty("heartbeat.interval.ms",parameterTool.get("heartbeat.interval.ms"));
        kafkaConsumerProperties.setProperty("session.timeout.ms", parameterTool.get("session.timeout.ms"));
        kafkaConsumerProperties.setProperty("enable.auto.commit", parameterTool.get("enable.auto.commit"));
        kafkaConsumerProperties.setProperty("auto.offset.reset", parameterTool.get("auto.offset.reset"));
        
        FlinkKafkaConsumer010<TaxiPosition> taxiPositionResource = new FlinkKafkaConsumer010<> (parameterTool.get("topic.input"), 
                                                                                                new DeserializationTaxiPosition(), 
                                                                                                kafkaConsumerProperties);
        
        DataStream<TaxiPosition> taxiPositionStream = env.addSource(taxiPositionResource).name("input kafka");
        
        taxiPositionStream
                .filter(new FilterCity()).name("filtro città")
                .keyBy("district")
                .flatMap(new FlatMapDistrictInfo()).name("elaborazione")
                .map(new MapJsonDistrictInfo()).name("deserializzazione")
                .addSink(new FlinkKafkaProducer010<>(parameterTool.get("bootstrap.servers"), 
                        parameterTool.get("topic.output"), 
                        new SimpleStringSchema())).name("output kafka");
        
        env.execute("Taxi Analyzer " + parameterTool.get("city"));
        
    }
}
