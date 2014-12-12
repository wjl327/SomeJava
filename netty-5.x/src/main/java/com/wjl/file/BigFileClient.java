package com.wjl.file;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 * 官网没有找到例子，按照doc和api写出来的。
 * 简单二进制文件传输，没使用任何协议，所以只传输了文件的字节流，不包含任何文件META。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 * 
 */
public class BigFileClient {

	private static int PORT = 8000;
	private static String HOST = "localhost";
	// 保存接收到的文件 原来先不存在
	public static String FILENAME = "D:/mongodb-linux-x86_64-2.6.5.gz";

	private void connect() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.TCP_NODELAY, true)
			 .handler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new BigFileClientHandler());
				}
			});

			ChannelFuture f = b.connect(HOST, PORT).sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new BigFileClient().connect();
	}

}

class BigFileClientHandler extends ChannelHandlerAdapter {

	private BufferedOutputStream bos = null;
	private long receDataCount = 0;

	// 客户端和服务端TCP链路建立成功后，Netty会调用该方法
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		bos = new BufferedOutputStream(new FileOutputStream(BigFileClient.FILENAME));
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (bos != null) {
			bos.close();
			bos = null;
		}
		System.out.println("接收数据总长度：" + receDataCount);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		int len = buf.readableBytes();
		if(len <= 0)
			return;
		receDataCount+=len;
		byte[] data = new byte[len];
		buf.readBytes(data);
		bos.write(data);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.toString();
		ctx.close();
	}

}
