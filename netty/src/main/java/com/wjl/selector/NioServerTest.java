package com.wjl.selector;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;


public class NioServerTest {
	
	public static void main(String[] args) throws Exception{
		
		Selector selector = Selector.open();
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(8000));
		serverSocketChannel.configureBlocking(false);
		
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		while(selector.select() > 0){
			
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey selectionKey = it.next();
				it.remove();
				if(selectionKey.isAcceptable()){
					System.out.println("有个客户端连接来了！");
					ServerSocketChannel ss = (ServerSocketChannel)selectionKey.channel();
					ss.accept();
				}
			}
			
		}
		
	}

}
