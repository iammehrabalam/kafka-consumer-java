package com.gsoctask.syslog;

import com.google.common.io.Resources;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;


/*
 *   Consumer
 */

public class Consumer {

    /*
     * This method load props( say configuration)
     *
     * @fileName   given props file (config file)
     * @return     Properties object having key, value
     */
    public static Properties getConfig(String fileName) throws IOException{
        try (InputStream props = Resources.getResource(fileName).openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            return properties;

        }
    }

    /*
     * This method parse commandline arguments
     *
     * @args  string array contain command line arguments
     * @return Namespace object containing parsed arguments
     */
    private static Namespace argParse(String[] args){
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Consumer")
                .defaultHelp(true)
                .description("Specify topic(s) to listen by consumer");
        parser.addArgument("-topics", "--topics").required(true)
                .help("Specify topic(s) to listen");
        Namespace ns = null;

        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        return ns;
    }

    /*
     * main method
     */
    public static void main(String[] args) throws Exception {

        Namespace ns;
        Properties properties;
        String host;
        int port = 8000;
        boolean ack;

        KafkaConsumer<String, String> consumer;

        // client configuration from client.props
        properties = getConfig("client.props");
        if (properties.getProperty("host") == null || properties.getProperty("port") == null){
            throw new Exception("Client host and port required");
        }

        host = properties.getProperty("host");
        try {
            port = Integer.parseInt(properties.getProperty("port"));
        }
        catch (NumberFormatException e){
            System.out.println("port must be integer");
            System.exit(1);
        }

        // consumer configuration from consumer.props
        properties = getConfig("consumer.props");
        if (properties.getProperty("group.id") == null) {
            properties.setProperty("group.id", "syslog-ng");
        }
        consumer = new KafkaConsumer<>(properties);

        // getting topics from commandline arguments
        ns  = argParse(args);
        consumer.subscribe(Arrays.asList(ns.getString("topics").split(",")));

        // Making non blocking client object
        TCPNIOClient client = new TCPNIOClient(host, port);

        System.out.println("consumer started..");
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(200);
            for (ConsumerRecord<String, String> record : records) {
                client.send("topic::" + record.topic() +
                            " offset::" + record.offset() +
                            " data::" + record.value() + "\n");

            }

        }

    }
}
