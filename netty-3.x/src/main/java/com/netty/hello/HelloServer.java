package com.netty.hello;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class HelloServer {

	public static void main(String args[]) {
		// Server服务启动器
		ServerBootstrap bootstrap = new ServerBootstrap();

		// Nio(New I/0) 模式
		ChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newFixedThreadPool(4));

		// Oio(Old I/0) 模式
		// ChannelFactory channelFactory = new OioServerSocketChannelFactory(
		// Executors.newCachedThreadPool(),
		// Executors.newCachedThreadPool());

		// 设置一个处理客户端消息和各种消息事件的类(Handler)
		ChannelPipelineFactory channelPipelineFactory = new ChannelPipelineFactory() {

			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new HelloServerHandler());
			}
		};

		bootstrap.setFactory(channelFactory);
		bootstrap.setPipelineFactory(channelPipelineFactory);
		//服务端启动监听8000端口
		bootstrap.bind(new InetSocketAddress(8000));
	}

	private static class HelloServerHandler extends SimpleChannelHandler {
		//一旦有客户端链接，即完成channel链接，就会调用该方法
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			System.out.println("有个客户端链接上来了! " + ctx.getChannel().getRemoteAddress());
			String str = "欢迎  to Netty World!";
			ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
			buffer.writeBytes(str.getBytes());
			e.getChannel().write(buffer);//客户端和服务端发送数据都是通过channelBuffer。类似nio的ByteBuffer。
		}

	}
}