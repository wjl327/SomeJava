package com.nio.echo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;


public class EchoServer {
	
	public static final int READ_BUF_SIZE = 2048;
	
	private Selector selector;
	private Charset charset;
	
	public EchoServer(){
		try {
			selector = Selector.open();
			charset = Charset.forName("UTF-8");
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(8000));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * 监听者方法
	 */
	public void service(){
		while(true){
			try {
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while(it.hasNext()){
					SelectionKey key = it.next();
					it.remove();
					try {
						if(key.isAcceptable()){
							accept(key);
						}else if(key.isReadable()){
							echoMsg(key);
						}
					} catch (Exception e) {
						e.printStackTrace();
						if(key != null){
							key.cancel();
							key.channel().close();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	private void echoMsg(SelectionKey key) throws Exception {
		SocketChannel socketChannel = (SocketChannel)key.channel();
		ByteBuffer readBuf = ByteBuffer.allocate(READ_BUF_SIZE);
		int len = socketChannel.read(readBuf);
		if(len < 0){
			key.cancel();
			key.channel().close();
		}
		else if(len > 0){
			String rece = decode(readBuf);
			System.out.printf("client[%s] say :%s \n", socketChannel.socket().getRemoteSocketAddress() , rece);
			String msg = " This content is OK ["+ rece + "]";
			sendMsgToClient(socketChannel, msg);
		}
		else{
		}
	}

	/**
	 * 处理服务端收到连接请求的事件
	 * 
	 * @param key
	 * @throws Exception
	 */
	private void accept(SelectionKey key) throws Exception{
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
		sendMsgToClient(socketChannel, "Welcome to Nio World!");
	}

	/**
	 * 处理服务端收到读取请求的事件
	 * 
	 * @param socketChannel
	 * @param msg
	 * @throws Exception
	 */
	private void sendMsgToClient(SocketChannel socketChannel, String msg) throws Exception{
		ByteBuffer byteBuffer = encode(msg);
		while(byteBuffer.hasRemaining())
			socketChannel.write(byteBuffer);
	}
	
	private ByteBuffer encode(String msg){
		return charset.encode(msg);
	}
	
	private String decode(ByteBuffer byteBuffer){
		byteBuffer.flip();
		return charset.decode(byteBuffer).toString();
	}

	public static void main(String[] args) {
		new EchoServer().service();
	}

}
