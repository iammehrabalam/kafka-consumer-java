# Kafka Consumer in Java


## How to run consumer

Make sure you change configuration according to requirement [(configs or resources)](https://github.com/iammehrabalam/kafka-consumer-java/tree/master/src/main/resources)

for consumer configuration edit
[consumer.props](https://github.com/iammehrabalam/kafka-consumer-java/blob/master/src/main/resources/consumer.props)

for tcp host edit 
[client.props](https://github.com/iammehrabalam/kafka-consumer-java/blob/master/src/main/resources/client.props)

to run consumer 
```java
java Consumer --topics topic1,topic2
// topic1, topic2 are topics where consumer will listen
```
