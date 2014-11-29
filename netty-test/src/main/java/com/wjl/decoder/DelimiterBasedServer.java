package com.wjl.decoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 学习的使用分隔符解码器：DelimiterBasedFrameDecoder。也可以解决粘包和拆包的问题。
 * 遇到指定的"$$"分隔符就会解码为一行。 注释掉  ch.pipeline().addLast(
 * new DelimiterBasedFrameDecoder(2048, delimiter));这一行的话，基本就会看到tcp粘包的情况。
 * 
 * @author jl.wu
 *
 */
public class DelimiterBasedServer {
	
	private int port = 8000;
	
	private void service() throws Exception {
		//配置服务端的NIO线程组，分主线程和工作线程
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				 .channel(NioServerSocketChannel.class)
				 .option(ChannelOption.SO_BACKLOG, 1024)
				 .childHandler(new DelimiterBasedServerChildHandler());
			//绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally{
			//释放连接池
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private static class DelimiterBasedServerChildHandler extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ByteBuf delimiter = Unpooled.copiedBuffer("$$".getBytes());
			ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048, delimiter)); //遇到$$则解析为一行
			ch.pipeline().addLast(new StringDecoder());  //将ByteBuf解码称String
			ch.pipeline().addLast(new DelimiterBasedServerChannel());
		}
	}
	
	private static class DelimiterBasedServerChannel extends ChannelHandlerAdapter{
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			String body = (String)msg;
			System.out.println("收到客户端发来消息：" + body);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new DelimiterBasedServer().service();
	}

}
