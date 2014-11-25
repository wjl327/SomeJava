package com.netty.seg;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;


public class SegmentServer {
	
	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.setFactory(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new SegServerHandler());
			}
		});
		bootstrap.bind(new InetSocketAddress(8000));
	}
	
	private static class SegServerHandler extends SimpleChannelHandler {
		//测试TCP是基于流的发送！ 服务端每次读取5字符。
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			ChannelBuffer buffer = (ChannelBuffer)e.getMessage();
			while(buffer.readableBytes() >= 5){
				ChannelBuffer tempBuffer = buffer.readBytes(5);
				System.out.println(tempBuffer.toString(Charset.defaultCharset()));
			}
			if(buffer.readableBytes() > 0)
				System.out.println(buffer.toString(Charset.defaultCharset()));
		}
	}
	
}
