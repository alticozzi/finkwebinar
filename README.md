# finkwebinar
Java codes for running the Flink webinar.

SimulatorTaxi is a basic Java program that publishes on Kafka (non-deterministic events) the current position of a city taxi. DistrictAnalyzer program is a flow processor that uses the Apache Flink Framework to read from Kafka, determine real time amount of taxis belonging to all districts of a city. The process can be configured to monitor a single city. The monitoring is published on Kafka.
