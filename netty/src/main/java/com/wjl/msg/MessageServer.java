package com.wjl.msg;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Echo服务器
 * 
 */
public class MessageServer {
	
	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.setFactory(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new MessageServerHandler());
			}
		});
		bootstrap.bind(new InetSocketAddress(8000));
	}

}

class MessageServerHandler extends SimpleChannelHandler{
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		//e.getChannel().write();
		String str = "Welcome to Netty World!";
		ChannelBuffer buffer = ChannelBuffers.buffer(str.length());
		buffer.writeBytes(str.getBytes());
		e.getChannel().write(buffer);
	}
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer buf = (ChannelBuffer) e.getMessage();
		String rece = buf.toString(Charset.defaultCharset());
		System.out.println("client say : " + rece);
		String msg = " This content is OK ["+ rece + "]";
		ChannelBuffer buffer = ChannelBuffers.buffer(msg.length());
		buffer.writeBytes(msg.getBytes());
		e.getChannel().write(buffer);
	}
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		System.out.println("有个客户端关闭连接了。。。。。。");
	}
}
