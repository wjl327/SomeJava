package com.wjl.decoder.line;

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
import io.netty.util.CharsetUtil;

/**
 * 展示行编码LineBasedFrameDecoder的使用。遇到回车换行则解析。
 * 这样客户端和服务端每条消息都带上回车换行符，用来表示一条消息的结束，因此也避免TCP拆包和粘包的问题。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 * 
 */
public class LineDecoderServer {
	
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
								new LineBasedFrameDecoder(1024), //以换行符为结束标志的解码器,设置一个最长长度，以免内存溢出
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
		new LineDecoderServer().service();
	}
	
}

class HelloStringServerHandler extends ChannelHandlerAdapter{
	private int counter = 1;
	private final String CR = System.getProperty("line.separator");
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.printf("收到第%d条消息%s\n", counter++, 
					((ByteBuf)msg).toString(CharsetUtil.UTF_8));
		//收到就回复OK,带上回车换行
		String rst = "OK" + CR;
		ctx.writeAndFlush(Unpooled.copiedBuffer(rst.getBytes()));
	}
	
}

