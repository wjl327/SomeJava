package com.netty.echo;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * Echo客户端,可以跟nio版本的实现进行对比
 * 客户端长连接 控制台输入后回车即发送给服务器
 * 
 */
public class EchoClient {
	
	public static void main(String[] args) {
		ClientBootstrap clientBootstrap = new ClientBootstrap();
		clientBootstrap.setFactory(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool()));
		clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new MessageClientHandler());
            }
        });
        // 连接到本地的8000端口的服务端
        clientBootstrap.connect(new InetSocketAddress("127.0.0.1", 8000));
	}

}

class MessageClientHandler extends SimpleChannelHandler{
	private Channel channel;
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		this.channel = e.getChannel();
		new Thread(new ReadAndSend(channel)).start();
	}
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer buf = (ChannelBuffer)e.getMessage();
		System.out.println(buf.toString(Charset.defaultCharset()));
	}
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		System.out.println("client closed!!! bye bye !!!");
		System.exit(0);
	}
	
}

class ReadAndSend implements Runnable{
	private Channel channel;
	private Scanner scanner = new Scanner(System.in);
	public ReadAndSend(Channel channel) {
		this.channel=channel;
	}
	public void run() {
		while(true){
			String line = scanner.nextLine();
			if(line.equals("exit")){
				channel.close();
				break;
			}
			ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
			buffer.writeBytes(line.getBytes());
			channel.write(buffer);
		}
	}
}
