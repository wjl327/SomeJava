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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileInputStream;

/**
 * netty-5.0.0.Alpha1版本的例子
 * https://github.com/netty/netty/blob/netty-5.0.0.Alpha1/example/src/main/java/io/netty/example/filetransfer/FileServer.java
 * 
 * 通过StringEncoder和LineBasedFrameDecoder来传输文件。
 * 
 */
public class FileServer1 {
	
	private int port = 8000;
	
	private void service() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, 1024)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .childHandler(new ChannelInitializer<SocketChannel>(){
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8)); //支持ctx写字符串，不用读写ByteBuf  
					ch.pipeline().addLast(new LineBasedFrameDecoder(8192)); //以换行符为结束标志的解码器
					ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8)); //将接收到的对象转字符串
					ch.pipeline().addLast(new StringServerHandler());
				}
			 });
			
			// Start the server.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
		
	public static void main(String[] args) throws Exception {
		new FileServer1().service();
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
		if(file.exists()){
			FileInputStream fis = new FileInputStream(file);
			FileRegion fileRegion = new DefaultFileRegion(fis.getChannel(), 0, file.length());
			ctx.write(fileRegion);
			ctx.writeAndFlush(CR);
			fis.close();
		}else{
			ctx.writeAndFlush("File Not Found: " + fileName + CR);
		}
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
			throws Exception {
		cause.toString();
		ctx.close();
	}
}

