package com.wjl.seg;

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
			e.getChannel().write(tran("Hi"));
			e.getChannel().write(tran("I'm a"));
			e.getChannel().write(tran("seg "));
			e.getChannel().write(tran("client!"));
			e.getChannel().close();
			System.exit(0);
		}
		private ChannelBuffer tran(String str){
			ChannelBuffer cb = ChannelBuffers.buffer(str.length());
			cb.writeBytes(str.getBytes());
			return cb;
		}
	}

}
