package com.wjl.selector;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


public class NioClientTest {
	
	public static void main(String[] args) throws Exception{
		
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.socket().connect(new InetSocketAddress("localhost", 8000));
		
	}

}
