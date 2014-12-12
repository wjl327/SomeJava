package com.wjl.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty5.x 入门Demo
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 *
 */
public class HelloClient {

	private int port = 8000;
	private String host = "localhost";

	private void connect() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.TCP_NODELAY, true)  //客户端参数就option 没有childoption的概念。
			 .handler(new ChannelInitializer<SocketChannel>(){
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new HelloClientHandler());
					}
				});
			// 绑定端口，同步等待成功
			ChannelFuture f = b.connect(host, port).sync();
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			// 释放连接池
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new HelloClient().connect();
	}

}

class HelloClientHandler extends ChannelHandlerAdapter {

	//收到一个读事件的时候会触发该方法
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		System.out.println(new String(req, "UTF-8"));
		
		ChannelFuture future = ctx.close(); // 关闭channel,即断开客户端和服务的链接
		
		//由于Netty所有操作都是异步的，所以执行完ctx.close()，并不一定关闭成功。要知道关闭结果就要设置回调监听器。
		//如果不需要在关闭完之后做操作，这一步可以不用。这里只是为了展示如何监听netty异步操作的结果。 
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println("客户端成功关闭链接。");
			}
		});
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
