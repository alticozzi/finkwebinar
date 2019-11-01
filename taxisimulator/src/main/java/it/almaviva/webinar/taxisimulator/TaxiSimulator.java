package it.almaviva.webinar.taxisimulator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.almaviva.webinar.taxisimulator.kafka.Producer;
import it.almaviva.webinar.taxisimulator.model.districtdto.DistrictConf;
import it.almaviva.webinar.taxisimulator.model.districtdto.DistrictDTO;
import it.almaviva.webinar.taxisimulator.model.taxi.TaxiPOJO;
import it.almaviva.webinar.taxisimulator.model.taxi.TransitTypeEnum;
import it.almaviva.webinar.taxisimulator.model.taxiinfo.TaxiInfo;



public class TaxiSimulator {

    private static final int MAX_PRIORITY = 10;
    private static final int SLOW_VELOCITY = 2;  // da 1 a 5
    private static final String KAFKA_PROPERTIES = "kafka.properties";
    private static final Logger logger = Logger.getLogger(TaxiSimulator.class);

    public static void main(String[] args)
    {
        String city  = null;
        DistrictConf districtConf = null;

        if(args.length == 0) {
            logger.error("The argument is missing. Define the city to be simulated");
            return;
        } else {
            city = args[0];
        }
        
        logger.info("Start taxi simulator for the city " + city + "...");
        
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(TaxiSimulator.class.getClassLoader().getResourceAsStream(city + "/district.json")));
            districtConf = new ObjectMapper().readValue(bufferedReader, DistrictConf.class);
        } catch (Exception e) {
            logger.error("Error in reading the file " + city + "/district.json - message -> " + e.getMessage());
            return;
        }
        
        logger.info("Load District configuration " + city + ". Number of taxi = " + districtConf.getNumTaxi() + ", number of district = " + districtConf.getDistricts().size());
        List<TaxiInfo> overview = getNewOverview(districtConf);
        logger.info("Init state:" + overview);
        logger.info("---------------- Start simulation ----------------");
        Producer kafkaProducer = getKafkaProducer();
        simulation(city, districtConf, overview, 0, new Random(), kafkaProducer);
    }


    private static void simulation(String city, DistrictConf districtConf, List<TaxiInfo> overview, int index, Random rand, Producer kafkaProducer) {
        while(index < overview.size()) {
            index = ( ( index != overview.size() - 1 ) ? ( index + 1 ) : 0 );
            TaxiInfo taxiInfo = overview.get(index);
            if(OffsetDateTime.now().isAfter(taxiInfo.getNextTrigger())) {
                String taxiId = taxiInfo.getId();
                TransitTypeEnum transitType = taxiInfo.getTransitType();
                taxiInfo.setCurrentNumTic(taxiInfo.getCurrentNumTic() - 1);
                if(taxiInfo.getCurrentNumTic() == 0) {
                    DistrictDTO district = null;
                    int currentPriority = taxiInfo.getCurrentPriority();
                    int newPriority = rand.nextInt(MAX_PRIORITY) + 1;
                    if(newPriority > currentPriority)  { //leave
                        TaxiPOJO taxiPOJO = getTaxiPOJO(taxiInfo, city, TransitTypeEnum.LEAVE);
                        kafkaProducer.publish(taxiPOJO);
                        district = getRandomDistrict(districtConf.getDistricts());
                    } else { //run
                        if(changeRun()) {
                            TransitTypeEnum newTransitType = getOtherTransitType(transitType);
                            taxiInfo.setTransitType(newTransitType);
                        }
                        TaxiPOJO taxiPOJO = getTaxiPOJO(taxiInfo, city, taxiInfo.getTransitType());
                        kafkaProducer.publish(taxiPOJO);
                        district = new DistrictDTO();
                        district.setName(taxiInfo.getCurrentDistrict());
                        district.setPriority(taxiInfo.getCurrentPriority());
                    }
                    setNewTaxiInfo(taxiInfo, district, taxiId, taxiInfo.getTransitType());
                } else {
                    taxiInfo.setNextTrigger(getRandomNextTriggerd());
                }
            }
        }
    }


    private static Producer getKafkaProducer() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream(KAFKA_PROPERTIES)) {
            props.load(resourceStream);
            return new Producer(props.getProperty("bootstrap.servers"), props.getProperty("topic"));
        } catch (Exception e) {
            return new Producer(null, null);
        }
    }

    private static TaxiPOJO getTaxiPOJO(TaxiInfo taxiInfo, String city, TransitTypeEnum transitType) {
        TaxiPOJO taxiPOJO = new TaxiPOJO();
        taxiPOJO.setId(taxiInfo.getId());
        taxiPOJO.setDistrict(taxiInfo.getCurrentDistrict());
        taxiPOJO.setCity(city);
        taxiPOJO.setTransitType(transitType);
        taxiPOJO.setTimestamp(new Date(OffsetDateTime.now().toEpochSecond()));
        return taxiPOJO;
    }

    private static List<TaxiInfo> getNewOverview(DistrictConf districtConf) {
        List<TaxiInfo> newOverview = new ArrayList<>();
        for(int i = 0; i < districtConf.getNumTaxi(); i++) {
            TaxiInfo taxiInfo = new TaxiInfo();
            setNewTaxiInfo(taxiInfo, getRandomDistrict(districtConf.getDistricts()), getRandomTaxiId(), getRandomTransitTypeEnum());
            newOverview.add(taxiInfo);
        }
        return newOverview;
    }

    private static void setNewTaxiInfo(TaxiInfo taxiInfo, DistrictDTO district, String taxiId, TransitTypeEnum transitTypeEnum) {
        Random rand = new Random();
        taxiInfo.setId(taxiId);
        taxiInfo.setCurrentDistrict(district.getName());
        taxiInfo.setCurrentPriority(district.getPriority());
        taxiInfo.setCurrentNumTic(rand.nextInt(10) + 2);
        taxiInfo.setNextTrigger(getRandomNextTriggerd());
        taxiInfo.setTransitType(transitTypeEnum);
    }


    private static OffsetDateTime getRandomNextTriggerd() {
        Random rand = new Random();
        return OffsetDateTime.now().plusSeconds((int)(rand.nextInt(SLOW_VELOCITY) + 1));
    }

    private static DistrictDTO getRandomDistrict(List<DistrictDTO> districts) {
        Random rand = new Random();
        for(int i = rand.nextInt(districts.size()); i < districts.size(); i = ( ( i != districts.size() - 1 ) ? ( i + 1 ) : 0 ) ) {
            Integer valuePriority = rand.nextInt(MAX_PRIORITY) + 1;
            DistrictDTO district = districts.get(i);
            if(district.getPriority() >= valuePriority)
                return district;
        }
        return new DistrictDTO();
    }

    private static String getRandomTaxiId() {
        Random rand = new Random();
        String alphabet = "ABCDEFGHJKILMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 5; i++) {
            if(rand.nextBoolean()) {
                sb.append(alphabet.charAt(rand.nextInt(25)));
            } else {
                Integer value = rand.nextInt(10);
                sb.append(value.toString());
            }
        }
        return sb.toString();
    }

    private static TransitTypeEnum getRandomTransitTypeEnum() {
        return new Random().nextInt(2) == 0 ? TransitTypeEnum.BUSY : TransitTypeEnum.FREE;
    }

    private static TransitTypeEnum getOtherTransitType(TransitTypeEnum transitTypeEnum) {
        if(TransitTypeEnum.BUSY.equals(transitTypeEnum))
            return TransitTypeEnum.FREE;
        if(TransitTypeEnum.FREE.equals(transitTypeEnum))
            return TransitTypeEnum.BUSY;
        return transitTypeEnum;
    }

    private static boolean changeRun() {
        Random rand = new Random();
        int firstValue = rand.nextInt(7);
        int secondValue = rand.nextInt(7);
        return firstValue == secondValue;
    }
}
