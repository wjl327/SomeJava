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

import java.util.Date;

/**
 * 这是暂时错误例子：通过时间服务器的例子，测试TCP粘包和拆包问题，也称“读半包、写半包”。  
 * 这里演示了读写半包的问题，因此服务端并不会收到100个请求消息，通过counter可以看到只收到一两条。
 * 
 * finish包下的两个类则是通过LineBasedFrameDecoder换行解码器来解决该问题的。
 * 另外通过FixedLengthFrameDecoder和DelimiterBasedFrameDecoder都可以解决问题。
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
		ByteBuf buf = (ByteBuf)msg;  //netty5.0 
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8")
			.substring(0, req.length - System.getProperty("line.separator").length());
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

