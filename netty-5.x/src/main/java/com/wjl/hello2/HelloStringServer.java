package com.wjl.hello2;

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

/**
 * 该客户端用于测试TCP粘包和拆包问题，也称“读半包、写半包”。  
 * 这里演示了客户端连上服务器之后，客户端就会发送100个请求消息，测试服务端是否收到100消息，并回复100次0K。
 * 
 * 当然，这里几乎不可能成功完成100交互。原因是由于TCP在发送的时候是流的方式，所以可能多个包"粘"在一起发，也有可能一个包拆开发。
 * 
 * 后续可以通过自己解析来解决该问题，也可以通过LineBasedFrameDecoder或者FixedLengthFrameDecoder等解码器来完成。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 * 
 */
public class HelloStringServer {
	
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
						ch.pipeline().addLast(new HelloStringServerHandler());
					}
			});
			
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
		
	
	
	public static void main(String[] args) throws Exception {
		new HelloStringServer().service();
	}
	
}

class HelloStringServerHandler extends ChannelHandlerAdapter{
	
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("服务端收到客户端:" + body);
		//收到就回复OK
		String rst = "OK";
		ctx.writeAndFlush(Unpooled.copiedBuffer(rst.getBytes()));
	}
	
}

