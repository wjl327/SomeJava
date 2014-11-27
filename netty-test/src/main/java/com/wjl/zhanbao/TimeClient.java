package com.wjl.zhanbao;

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
 * hello Netty 5.0 客户端  没考虑读写半包 
 * 学习于《Netty权威指南》
 * 
 */
public class TimeClient {
	
	private void bind(int port) throws Exception {
		//配置服务端的NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.TCP_NODELAY, true)
			 .handler(new ChildChannelHandler());
			//绑定端口，同步等待成功
			ChannelFuture f = b.connect("localhost", port).sync();
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
		new TimeClient().bind(8000);
	}
	
}

class TimeClientHandler extends ChannelHandlerAdapter{
	
	private byte[] req;
	private int count;
	
	public TimeClientHandler(){
		req = ("QUERY TIME" + System.getProperty("line.separator")).getBytes();
	}
	
	//客户端和服务端TCP链路建立成功后，Netty会调用该方法
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf message = null;
		for(int i = 0 ; i < 100; i++){
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("Now is : " + body + " ; the counter is " + ++count);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

}

