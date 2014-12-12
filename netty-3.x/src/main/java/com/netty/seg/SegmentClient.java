package com.netty.seg;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * 了解TCP拆包和粘包的问题。
 * @author jl.wu
 *
 */
public class SegmentClient {
	
	public static void main(String[] args) {
		ClientBootstrap bootstrap = new ClientBootstrap();
		bootstrap.setFactory(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new SegClientHandler());
			}
		});
		bootstrap.connect(new InetSocketAddress("127.0.0.1", 8000));
	}
	
	private static class SegClientHandler extends SimpleChannelHandler {
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			e.getChannel().write(tran("Hello, ser world!"));
			e.getChannel().write(tran("I'm a seg client!"));
			e.getChannel().write(tran("Hello, ccc world!"));
			e.getChannel().write(tran("I'm a bbb client!"));
			e.getChannel().close();
			System.out.println("客户端发送完毕!");
			System.exit(0);
		}
		private ChannelBuffer tran(String str){
			ChannelBuffer cb = ChannelBuffers.buffer(str.length());
			cb.writeBytes(str.getBytes());
			return cb;
		}
	}

}
