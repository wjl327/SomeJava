package com.netty.obj;

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

/**
 * 最简单的编/解码器使用。基于Java序列化的ObjectEncoder和ObjectDecoder
 * 
 * @author jl.wu
 *
 */
public class ObjectServer {
	
	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.setFactory(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new ObjectEncoder(),  //对象编码器。将对象编码成ChannelBuffer发送
						new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())), //对象解码器，将接收到的ChannelBuffer序列化为对象
						new ObjectServerHandler()); //业务逻辑handler只需要处理对象。
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

