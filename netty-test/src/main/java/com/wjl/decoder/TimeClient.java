package com.wjl.decoder;

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
 * 这是暂时错误例子：通过时间服务器的例子，测试TCP粘包和拆包问题，也称“读半包、写半包”。  
 * 这里演示了读写半包的问题，客户端发送100个请求消息，测试服务端是否收到100条并正确响应100个回复。
 * 
 * finish包下的两个类则是通过LineBasedFrameDecoder换行解码器来解决该问题的。
 * 另外通过FixedLengthFrameDecoder和DelimiterBasedFrameDecoder都可以解决问题。
 * 
 *                                    -- 参考于《Netty权威指南》
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
	
	private int counter = 0;
	//命令字符串，带回车换行符
	private byte[] req = ("query time" + System.getProperty("line.separator")).getBytes();
	
	//客户端和服务端TCP链路建立成功后，Netty会调用该方法
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf buf = null;
		for(int i = 0 ; i < 100; i++){
			buf = Unpooled.buffer(req.length);
			buf.writeBytes(req);
			ctx.writeAndFlush(buf);
		}
	}
	
	//客户端收到一个读事件的时候会触发该方法
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("当前时间为：" + body + "; 这是第" + ++counter + "条回复.");
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.toString();
		ctx.close();
	}

}

