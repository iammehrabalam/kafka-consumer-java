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
import java.net.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;


public class Consumer {

    // tcp client host and port
    private static String host;
    private static int port;


    /*
     * This method send data to server
     *
     * @str    contain message data
     */
    private static void tcpClient(String str) throws Exception{

        String response;
        Socket clientSocket = new Socket(host, port);
        DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        toServer.writeBytes(str + '\n');

        // getting response from server
        response = fromServer.readLine();
        System.out.println("FROM SERVER: " + response);
        clientSocket.close();

        // TODO handle the case when error occur
    }

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

        System.out.println("here");
        // Simple print messages received by consumer
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(200);
            for (ConsumerRecord<String, String> record : records) {
                tcpClient("topic::" + record.topic()
                          + " offset::" + record.offset()
                          + " data::" + record.value());
                System.out.println("send data");
            }

        }

    }
}
