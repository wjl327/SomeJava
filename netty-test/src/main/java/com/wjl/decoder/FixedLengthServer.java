package com.wjl.decoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 学习的使用定长解码器：FixedLengthFrameDecoder。也可以解决粘包和拆包的问题。
 * 定长消息解码服务器，这里设定解析长度为"30",因此该服务器不管TCP如何粘包、拆包都会保证解析为设定的长度的字符串数据.
 * 如果服务器解析到的长度还没达到设定长度，就不会触发channelRead方法。
 * 
 * 看下面代码注有 "该行注释可对比效果" 那一行，就是对FixedLengthFrameDecoder的使用，注释之后，就不能按照指定长度截取。
 * 即使客户端发的消息有50条，也可能被TCP合并为两三读取。
 * 
 * @author jl.wu
 *
 */
public class FixedLengthServer {
	
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
				 .childHandler(new FixedLengthChannelHandler());
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
	
	private static class FixedLengthChannelHandler extends ChannelInitializer<SocketChannel>{
		private int fixedLength = 30;
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new FixedLengthFrameDecoder(fixedLength));  //该行注释可对比效果
			ch.pipeline().addLast(new StringDecoder());  //将ByteBuf解码称String
			ch.pipeline().addLast(new FixedLengthServerChannel());
		}
	}
	
	private static class FixedLengthServerChannel extends ChannelHandlerAdapter{
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			String body = (String)msg;
			System.out.println("收到客户端发来消息：" + body);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new FixedLengthServer().service();
	}

}
