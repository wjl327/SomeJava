package com.wjl.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 非二进制的文件传输，只是通过字符串传输文件内容。
 * 通过StringEncoder和LineBasedFrameDecoder传输文件内容.
 * 
 */
public class FileServer {
	
	private int port = 8000;
	
	private void service() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, 1024)
			 .childHandler(new ChannelInitializer<SocketChannel>(){
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ChunkedWriteHandler()); //文件传输
					ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8)); //将接收到的对象转字符串
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new FileServerHandler());
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
		new FileServer().service();
	}
	
}

class FileServerHandler extends SimpleChannelInboundHandler<String>{

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		String fileName = msg;
		System.out.println("客户端请求的文件：" + fileName);
		File file = new File("html/" + fileName);
		if(!file.exists()){
			System.out.println("文件不存在!");
			return;
		}
		
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileRegion fileRegion = new DefaultFileRegion(raf.getChannel(), 0, raf.length());
		ChannelFuture writeFuture = ctx.write(fileRegion, ctx.newProgressivePromise());
		writeFuture.addListener(new ChannelProgressiveFutureListener() {
			
			public void operationComplete(ChannelProgressiveFuture future)
					throws Exception {
				System.out.println("Transfer complete.");
			}
			
			public void operationProgressed(ChannelProgressiveFuture future,
					long progress, long total) throws Exception {
				if(total < 0){
					System.err.println("Transfer progress:" + progress);
				} else{
					System.err.println("Transfer progress:" + progress + "/" + total);
				}
			}
		});
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
			throws Exception {
		cause.toString();
		ctx.close();
	}
}

