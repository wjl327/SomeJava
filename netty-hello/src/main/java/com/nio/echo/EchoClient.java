package com.nio.echo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;


public class EchoClient {
	
public static final int READ_BUF_SIZE = 2048;
	
	private Selector selector;
	private Charset charset;
	private volatile boolean stop = false;
	
	public EchoClient(){
		try {
			selector = Selector.open();
			charset = Charset.forName("UTF-8");
			SocketChannel socketChannel = SocketChannel.open();
			//socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8000));
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
			new Thread(new ReadAndSend(socketChannel)).start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * 监听者方法
	 */
	public void service(){
		while(!stop){
			try {
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while(it.hasNext()){
					SelectionKey key = it.next();
					it.remove();
					try {
						if(key.isReadable()){
							readMsg(key);
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
	
	public void stop(){
		stop = true;
	}
	
	/**
	 * 处理客户端收到读取请求的事件
	 * 
	 * @param socketChannel
	 * @param msg
	 * @throws Exception
	 */
	private void readMsg(SelectionKey key) throws Exception {
		SocketChannel socketChannel = (SocketChannel)key.channel();
		ByteBuffer readBuf = ByteBuffer.allocate(READ_BUF_SIZE);
		int len = socketChannel.read(readBuf);
		if(len > 0){
			String rece = decode(readBuf);
			System.out.printf(rece);
		}
	}

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
		new EchoClient().service();
	}
	
	
	class ReadAndSend implements Runnable{
		private SocketChannel channel;
		private Scanner scanner = new Scanner(System.in);
		public ReadAndSend(SocketChannel channel) {
			this.channel=channel;
		}
		public void run() {
			while(true){
				String line = scanner.nextLine();
				if(line.equals("exit")){
					try {
						stop();
						channel.close();
						selector.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				try {
					sendMsgToClient(channel, line);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}

