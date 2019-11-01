package it.almaviva.webinar.flink.taxianalyzer;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.log4j.Logger;

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
    }
}
