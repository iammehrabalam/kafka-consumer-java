package com.gsoctask.syslog;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class TCPServer {
    public static void main(String[] args) throws Exception{
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while(true){
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream toClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = fromClient.readLine();
            System.out.println("Received: " + clientSentence);
            System.out.println("start sleeping");
            TimeUnit.SECONDS.sleep(10);
            System.out.println("after sleeping");
            capitalizedSentence = clientSentence.toUpperCase() + '\n';
            toClient.writeBytes(capitalizedSentence);
        }
    }
}
