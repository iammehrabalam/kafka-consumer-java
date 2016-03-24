package com.gsoctask.syslog;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


/*
 *  NIO Server for testing
 */

public class TCPNIOServer {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8000;
        Selector selector = Selector.open();
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        ssChannel.socket().bind(new InetSocketAddress(host, port));
        ssChannel.configureBlocking(false);
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            start(selector.selectedKeys());
        }
    }

    public static void start(Set readySet) throws Exception {
        Iterator iterator = readySet.iterator();
        System.out.println(iterator.hasNext());
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();

            if (key.isAcceptable()) {
                ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                SocketChannel sChannel = ssChannel.accept();
                sChannel.configureBlocking(false);
                sChannel.register(key.selector(), SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                String msg = readMessage(key);
                System.out.println("RECEIVED STRING::" + msg);
            }
            iterator.remove();


        }
    }

    public static String readMessage(SelectionKey key) throws Exception {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        int bytesCount = sChannel.read(buffer);
        if (bytesCount > 0) {
            return new String(buffer.array()).trim();
        }
        return "empty";
    }
}
