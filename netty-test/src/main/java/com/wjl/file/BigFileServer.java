package com.wjl.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;

/**
 * 官网没有找到例子，按照doc和api写出来的。
 * 简单二进制文件传输，没使用任何协议，所以只传输了文件的字节流，不包含任何文件META。
 * 
 */
public class BigFileServer {
	
	//传输文件
	public static String FILENAME = "E:/developer/mongodb-linux-x86_64-2.6.5.gz";
	private int port = 8000;
	
	private void service() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, 100)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .childHandler(new ChannelInitializer<SocketChannel>(){
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new ChunkedWriteHandler(),new BigFileServerHandler());
				}
			 });
			ChannelFuture f = b.bind(port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
		
	public static void main(String[] args) throws Exception {
		new BigFileServer().service();
	}
	
}

class BigFileServerHandler extends ChannelHandlerAdapter{
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		File file = new File(BigFileServer.FILENAME);
		System.out.println("发送数据总长度：" + file.length());
		ChannelFuture cf = ctx.channel().writeAndFlush(new ChunkedFile(file)); //Netty任何IO操作都是异步
		cf.addListener(ChannelFutureListener.CLOSE); //异步监听器：发送结束的时候关闭channel，即关闭与客户端的链接。
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

