package com.netty.obj;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class ObjectClient {
	
	public static void main(String[] args) {
		ClientBootstrap bootstrap = new ClientBootstrap();
		bootstrap.setFactory(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new ObjectEncoder(),
						new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())),
						new ObjectClientHandler());
			}
		});
		bootstrap.connect(new InetSocketAddress("localhost",8000));
	}
	
	private static class ObjectClientHandler extends SimpleChannelHandler{
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			Command command = new Command();
			command.setActionName("hello action");
			e.getChannel().write(command);
		}
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			Command command = (Command)e.getMessage();
			System.out.println(command.getActionName());
			e.getChannel().close();
			System.exit(0);
		}
	}

}

