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
 * 客户端测试，给FixedLengthServer发送100个长度为30的字符串。
 * @author jl.wu
 *
 */
public class FixedLengthClient {
	
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
			 .handler(new FixedClientChannelHandler());
			//绑定端口，同步等待成功
			ChannelFuture f = b.connect(host, port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally{
			//释放连接池
			group.shutdownGracefully();
		}
	}
	
	private static class FixedClientChannelHandler extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new FixedLengthClientChannel()); //该管道无其他解码器，直接请求到我们的业务Channel。
		}
	}
	
	private static class FixedLengthClientChannel extends ChannelHandlerAdapter{
		String msg = "hello server , i am a  client.";  //长度30
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			ByteBuf buf = null;
			for(int i = 0; i < 50; i++){
				buf = Unpooled.buffer(msg.length());
				buf.writeBytes(msg.getBytes());
				ctx.writeAndFlush(buf);
			}
			ctx.close();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new FixedLengthClient().connect();
	}

}
