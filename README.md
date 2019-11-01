# finkwebinar
Java codes for running the Flink webinar.

SimulatorTaxi is a basic Java program that publishes on Kafka (non-deterministic events) the current position of a city taxi. DistrictAnalyzer program is a flow processor that uses the Apache Flink Framework to read from Kafka, determine in real time the amount of taxis belonging to all the districts of a city. The monitoring is published on Kafka.
