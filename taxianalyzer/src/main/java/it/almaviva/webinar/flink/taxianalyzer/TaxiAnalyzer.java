package it.almaviva.webinar.flink.taxianalyzer;

import java.util.Properties;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.log4j.Logger;
import it.almaviva.webinar.flink.taxianalyzer.deserialization.DeserializationTaxiPosition;
import it.almaviva.webinar.flink.taxianalyzer.model.taxi.TaxiPosition;

public class TaxiAnalyzer 
{
    private static final Logger logger = Logger.getLogger(TaxiAnalyzer.class);

    public static void main(String[] args) throws Exception {

        // Read properties file

        ParameterTool parameterTool = ParameterTool.fromPropertiesFile(TaxiAnalyzer.class.getClassLoader().getResourceAsStream("flink.properties"));
        logger.info(parameterTool.toMap());

        // Init flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /*
         * I programmi Flink vengono eseguiti nel contesto di un ambiente di esecuzione. 
         * Un ambiente di esecuzione definisce un parallelismo predefinito per tutti gli operatori, le origini dati e i sink di dati che esegue.*/
        env.setParallelism(4);

        // Set timeout in ms of buffer between activities
        env.setBufferTimeout(100);

        // Set the global configuration for the flink process with the propertyResources object
        env.getConfig().setGlobalJobParameters(parameterTool);

        /* Defines how the system determines the time to sort the operations.
         * IngestionTime indicates that the time of each single flow element is 
         * determined when the element enters the flow. */
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        // Disables the printing of progress update messages to System.out
        env.getConfig().disableSysoutLogging();

        // Kafka Consumer resource 
        Properties kafkaConsumerProperties = new Properties();
        kafkaConsumerProperties.setProperty("bootstrap.servers",  parameterTool.get("bootstrap.servers"));
        kafkaConsumerProperties.setProperty("group.id", parameterTool.get("group.id"));
        kafkaConsumerProperties.setProperty("heartbeat.interval.ms",parameterTool.get("heartbeat.interval.ms"));
        kafkaConsumerProperties.setProperty("session.timeout.ms", parameterTool.get("session.timeout.ms"));
        kafkaConsumerProperties.setProperty("enable.auto.commit", parameterTool.get("enable.auto.commit"));
        kafkaConsumerProperties.setProperty("auto.offset.reset", parameterTool.get("auto.offset.reset"));

        /* Il FlinkKafkaConsumer è uno Stream Data Resource che estrae un flusso di dati parallelo da Apache Kafka. . Il consumatore può 
         * eseguire più istanze parallele, ognuna delle quali estrarrà i dati da una o più partizioni Kafka*/
        FlinkKafkaConsumer010<TaxiPosition> taxiPositionResource = new FlinkKafkaConsumer010<> (parameterTool.get("topic.input"), new DeserializationTaxiPosition(), kafkaConsumerProperties);

        // FLINK JOB

        DataStreamSource<TaxiPosition> taxiPositionStream = env.addSource(taxiPositionResource);
        
        taxiPositionStream.print();
        
        env.execute("Taxi Analyzer");
    }
}
