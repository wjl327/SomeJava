package com.wjl.hello3;

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
import io.netty.util.CharsetUtil;

/**
 * 尝试自己解析来解决TCP粘包和拆包的问题。
 * 比如用固定长度来解析（这种受限于通信的消息永远都是定长字节数），再比如发送数据的时候都加上一个换行符，然后通过换行符来解析等等。
 * 
 * 这里我们试试用长度来解析。
 * 因为客户端每次都发送Hello world  所以服务端解析长度为11的消息
 * 因为服务端每次都回复OK  所以客户端解析长度为2的消息
 * 
 * 这里我们自己实现一个解码器。这样客户端和服务端逻辑都不用变。
 * 见第52行， 只需要在通道上加一个handler。（必须加在我们的handler前）
 * 
 * 实际上，只要参考api，我们可以实现各种自定义规则编解码器。当然，Netty已经帮我们封装好了好些编解码器，
 * 比如我们简单直接实现的这个HelloStringClient。Netty已经实现了，叫做定长解码器FixedLengthFrameDecoder。
 * 那么我们可以阅读FixedLengthFrameDecoder比较Netty的实现。  关于编解码器， 在decoder包展示。
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
						ch.pipeline().addLast(
								new HelloStringDecoder(11), //自己实现的解码器
								new HelloStringServerHandler()); //业务Handler
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
	private int counter = 1;
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.printf("收到第%d条消息%s\n", counter++, 
					((ByteBuf)msg).toString(CharsetUtil.UTF_8));
		//收到就回复OK
		String rst = "OK";
		ctx.writeAndFlush(Unpooled.copiedBuffer(rst.getBytes()));
	}
	
}

