package com.wjl.hello;

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
 * Netty5.x 入门Demo
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 *
 */
public class HelloServer {

	private int port = 8000;

	private void service() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // 用于服务端监听连接
		EventLoopGroup workerGroup = new NioEventLoopGroup();// 用于服务端处理读写事件
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // 指定创建Channel的例子
					.option(ChannelOption.SO_BACKLOG, 1024) // option相当于给Serversocket设置的参数
					.childOption(ChannelOption.SO_KEEPALIVE, true) // childoption相当于给接收到Socket设置的参数
					.childHandler(new ChannelInitializer<SocketChannel>() {

						protected void initChannel(SocketChannel ch) throws Exception { // 服务器接收到连接，初始化chnnale的时候会调用该方法
							ch.pipeline().addLast(new HelloServerHandler()); // 只有我们的业务handler
						}
					});

			ChannelFuture f = b.bind(port).sync(); // 绑定端口，同步等待成功
			f.channel().closeFuture().sync(); // 等待服务端监听端口关闭
		} finally {
			// 释放连接池
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new HelloServer().service();
	}

}

/**
 *  实际上我们Netty服务器上面的设置大部分时候通用的。
 *  剩下真正的业务其实都在我们直接实现的ChannelHandlerAdapter这个handler这里。
 */
class HelloServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.printf("客户端[%s]连接上来了！\n", ctx.channel().remoteAddress());
		ByteBuf buf = Unpooled.copiedBuffer("Hello client.".getBytes());
		ctx.writeAndFlush(buf);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.printf("客户端[%s]关闭连接了！\n", ctx.channel().remoteAddress());
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
