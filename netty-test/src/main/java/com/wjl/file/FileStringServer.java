package com.wjl.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 非二进制的文件传输，只是通过字符串传输文件内容。
 * 通过StringEncoder和LineBasedFrameDecoder传输文件内容.
 * 
 */
public class FileStringServer {
	
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
					ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8)); //支持ctx写字符串，不用读写ByteBuf  
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024)); //以换行符为结束标志的解码器
					ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8)); //将接收到的对象转字符串
					ch.pipeline().addLast(new StringServerHandler());
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
		new FileStringServer().service();
	}
	
}

class StringServerHandler extends SimpleChannelInboundHandler<String>{

	//必须换行符才LineBasedFrameDecoder才能解析成一行
	private static final String CR = System.getProperty("line.separator");
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		String fileName = msg;
		System.out.println("客户端请求的文件：" + fileName);
		File file = new File("html/" + fileName);
		if(!file.exists()){
			ctx.writeAndFlush("File Not Found: " + fileName + CR);
			return;
		}
		
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileRegion fileRegion = new DefaultFileRegion(raf.getChannel(), 0, raf.length());
		ctx.write(fileRegion);
		ctx.writeAndFlush(CR);
		raf.close();
		
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
			throws Exception {
		cause.toString();
		ctx.close();
	}
}

