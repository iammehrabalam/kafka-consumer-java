package com.gsoctask.syslog;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.BufferedReader;

/*
 *  Simple Socket client
 */

public class Client{
    Socket clientSocket;

    Client(String host, int port) throws Exception{
        this.clientSocket = new Socket(host, port);
    }

    public  void send(String str) throws Exception{

        String response;

        DataOutputStream toServer = new DataOutputStream(this.clientSocket.getOutputStream());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        toServer.writeBytes(str + '\n');

        // getting response from server
        response = fromServer.readLine();
        System.out.println("FROM SERVER: " + response);
        this.clientSocket.close();

    }

}
