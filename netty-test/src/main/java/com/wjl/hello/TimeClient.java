package com.wjl.hello;

import io.netty.bootstrap.Bootstrap;
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
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * hello Netty5.0 
 * 客户端  没考虑读写半包   -- 参考于《Netty权威指南》
 * 
 */
public class TimeClient {
	
	private String host = "localhost";
	private int port = 8000;
	
	private void connect() throws Exception {
		//配置服务端的NIO线程组，分主线程和工作线程
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.TCP_NODELAY, true)
			 .handler(new ChildChannelHandler());
			//绑定端口，同步等待成功
			ChannelFuture f = b.connect(host, port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally{
			//释放连接池
			group.shutdownGracefully();
		}
	}
		
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new TimeClientHandler());
		}
	}
	
	public static void main(String[] args) throws Exception {
		new TimeClient().connect();
	}
	
}

class TimeClientHandler extends ChannelHandlerAdapter{
	
	//客户端和服务端TCP链路建立成功后，Netty会调用该方法
	//通过这个方法，给时间服务器发送一个指令查询指令"query time"
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		1、通过ByteBuf缓冲区发送。
		ByteBuf buf = Unpooled.buffer(); //初始化一个默认缓冲区大小为256
		buf.writeBytes("query time".getBytes());
		ctx.writeAndFlush(buf);
//		2、测试直接发送字符串。  失败！！发送不了~~
//		ctx.writeAndFlush("query time");
	}

	//客户端收到一个读事件的时候会触发该方法
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("Now is : " + body);
		//结束
		ctx.close();
		System.exit(0);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
}

