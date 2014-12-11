package com.wjl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HelloServer {
	
	int port = 8080;
	
	private void service() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			 ServerBootstrap b = new ServerBootstrap();
			 b.group(bossGroup, workerGroup)
			  .channel(NioServerSocketChannel.class)
			  .option(ChannelOption.SO_BACKLOG, 128) 
			  .childOption(ChannelOption.SO_KEEPALIVE, true)
			  .childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new HelloServerHandler());
				}
			  });
			 
			 ChannelFuture f = b.bind(port).sync();
			 f.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception{
		new HelloServer().service();
	}
}

class HelloServerHandler extends ChannelHandlerAdapter {
	private static final String CR = System.getProperty("line.separator");
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for (int i = 0; i < 10; i++) {
			ByteBuf resp = Unpooled.copiedBuffer(("hello world - " + i).getBytes());
			ctx.write(resp);
		}
		ctx.write("").addListener(ChannelFutureListener.CLOSE);
		ctx.flush();
	}
}
