package com.gsoctask.syslog;

import java.io.*;
import java.net.*;

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
            capitalizedSentence = clientSentence.toUpperCase() + '\n';
            toClient.writeBytes(capitalizedSentence);
        }
    }
}
