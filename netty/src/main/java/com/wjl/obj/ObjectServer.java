package com.wjl.obj;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class ObjectServer {
	
	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.setFactory(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new ObjectEncoder(),
						new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())),
						new ObjectServerHandler());
			}
		});
		bootstrap.bind(new InetSocketAddress(8000));
	}
	
	private static class ObjectServerHandler extends SimpleChannelHandler{
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			Command command = (Command)e.getMessage();
			System.out.println(command.getActionName());
			Command newCommand = (Command)e.getMessage();
			newCommand.setActionName("success action");
			e.getChannel().write(newCommand);
		}
	}

}

