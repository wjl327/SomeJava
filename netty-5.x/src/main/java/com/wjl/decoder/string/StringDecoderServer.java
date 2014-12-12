package com.wjl.decoder.string;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 在已有LineBasedFrameDecoder的基础上
 * 增加：
 * new StringDecoder(CharsetUtil.UTF_8),//字符串解码器，将ByteBuf转成String，用于读入
 * new StringEncoder(CharsetUtil.UTF_8),//字符串编码器，将String转成ByteBuf，用于写出
 * 
 * 这样我们读取和写操作都可以直接用String。这样，业务类就更加简化了，不用再看到ByteBuf了。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 * 
 */
public class StringDecoderServer {
	
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
								new StringDecoder(CharsetUtil.UTF_8),//字符串解码器，将ByteBuf转成String，用于读入
								new StringEncoder(CharsetUtil.UTF_8),//字符串编码器，将String转成ByteBuf，用于写出
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
		new StringDecoderServer().service();
	}
	
}

class HelloStringServerHandler extends ChannelHandlerAdapter{
	private int counter = 1;
	private final String CR = System.getProperty("line.separator");
	private final String DATA = "OK" + CR;
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.printf("收到第%d条消息%s\n", counter++, (String)msg);
		//收到就回复OK,带上回车换行
		ctx.writeAndFlush(DATA);
	}
	
}

