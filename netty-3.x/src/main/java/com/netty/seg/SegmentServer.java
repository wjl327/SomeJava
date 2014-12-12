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

/**
 * 了解TCP拆包和粘包的问题。  及解决方法。
 * @author jl.wu
 *
 */
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
		
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			
			ChannelBuffer buffer = (ChannelBuffer)e.getMessage();
			
			//自行百度了解一下TCP拆包和粘包问题。
			
			/*1、直接读取的话，由于TCP基于流传输，会拆包和粘包问题。
			             客户端发了4个句子，但这个方法并不一定被调用4次。可能一次收到，也有可能两三次。
			             所以服务端收到的消息可能是连在一起的。 直接使用下面这一行接收试试  */
			 //System.out.println(buffer.toString(Charset.defaultCharset())); 
			 
			
			//2、解决TCP拆包和粘包的方法之一，由于客户端每次都是发17个字符长度的句子，所以可以按照字节长度读取数据包
			while(buffer.readableBytes() >= 17){
				ChannelBuffer tempBuffer = buffer.readBytes(17);
				System.out.println(tempBuffer.toString(Charset.defaultCharset()));
			}
			if(buffer.readableBytes() > 0)
				System.out.println(buffer.toString(Charset.defaultCharset()));
		
			//当然，这种方式只能解决长度一样的消息。比较简单。 实际上Netty提供了很多编解码的handler都可以用来解决该问题。
		}
	}
	
}
