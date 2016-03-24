
package com.gsoctask.syslog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPNIOClient{

    SocketChannel channel;

    /*
     * This constructor open the socket channel and setting non blocking true
     */
    TCPNIOClient(String host, int port) throws Exception{
        InetSocketAddress address = new InetSocketAddress(host, port);
        this.channel = SocketChannel.open(address);
        this.channel.configureBlocking(false);
    }

    /*
     *  This method write message into nio channel
     *  @str   message to send
     */
    public boolean send(String str) throws Exception{
        byte[] message = str.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        int bytes = this.channel.write(buffer);
        buffer.clear();

        System.out.println(bytes);
        if(str.length() == bytes){
            // System.out.println("success");
            return true;
        }
        else{
            // System.out.println("message cannot be write into channel");
            // TODO log message
            return false;
        }

    }

    /*
     *  this method close the channel
     */
    public void close() throws IOException{
        this.channel.close();
    }
}