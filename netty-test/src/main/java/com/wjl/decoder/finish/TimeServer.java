package com.wjl.decoder.finish;

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
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

/**
 * 通过LineBasedFrameDecoder(行解码器)和StringDecoder解决TCP粘包和拆包问题，也称“读半包、写半包”问题。
 * 现在服务端可以收到100个请求消息，因此也可以正确的回复。
 * 
 *                                    -- 参考于《Netty权威指南》
 */
public class TimeServer {
	
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
			 .childHandler(new ChildChannelHandler());
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
		
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024)); //以换行符为结束标志的解码器
			ch.pipeline().addLast(new StringDecoder()); //将接收到的对象转字符串
			ch.pipeline().addLast(new TimeServerHandler());
		}
	}
	
	public static void main(String[] args) throws Exception {
		new TimeServer().service();
	}
	
}

/**
 *	时间服务器 
 *
 */
class TimeServerHandler extends ChannelHandlerAdapter{
	
	private int counter = 0;

	//服务端收到一个读事件的时候会触发该方法
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		String body = (String)msg;
		System.out.println("时间服务器收到客户端指令:" + body + "; 这是第" + ++counter + "条指令");
		resend(ctx, body);
	}

	//回复客户端
	private void resend(ChannelHandlerContext ctx, String body) {
		String currentTime = "QUERY TIME".equalsIgnoreCase(body)?new Date().toString():"BAD ORDER";
		currentTime = currentTime + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(resp);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.toString();
		ctx.close();
	}

}

