package com.netty.hello;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class HelloClient {

	public static void main(String args[]) {
		//// Nio(New I/0) 模式启动器
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		
		// Oio(Old I/0) 模式启动器
		// ClientBootstrap bootstrap = new ClientBootstrap(
		// new OioClientSocketChannelFactory(
		// Executors.newCachedThreadPool()));
		
		//客户端和服务端一旦链接就会产生一个Channel。所有的事件都是经过Pipeline(管道)“传输”和处理。
		//管道则是又一连串的Handler组成的。这里只使用到了"HelloClientHandler",没用到其他handler，就我们的业务逻辑handler。
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new HelloClientHandler());
			}
		});
		
		//连接到本地的8000端口的服务端
		bootstrap.connect(new InetSocketAddress("127.0.0.1", 8000));
	}

	private static class HelloClientHandler extends SimpleChannelHandler {
		//一旦接收消息的事件就会调用该方法。
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			System.out.println(buf.toString(Charset.defaultCharset()));
			e.getChannel().close();
			System.exit(0);
		}
	}
}