package com.wjl.decoder.fixedlen;

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
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.util.CharsetUtil;

/**
 * 相比hello3包，这改了一行代码。
 * 将我们自己实现的HelloStringClient，替换成定长解码器FixedLengthFrameDecoder。
 * 同样可以解决TCP粘包和拆包的问题。但这个解码器只能用于网络通信消息是定长的协议。

 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 * 
 */
public class FixedLengthDecoderServer {
	
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
								new FixedLengthFrameDecoder(11), //定长解码器
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
		new FixedLengthDecoderServer().service();
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

