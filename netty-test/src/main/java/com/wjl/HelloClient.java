package com.wjl;

import java.util.List;

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
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HelloClient {
	private void connect() throws Exception {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>() {
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new HelloDecoder(), new HelloClientHandler());
				}
			});
			ChannelFuture f = b.connect("localhost", 8080).sync(); // (5)
			f.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception{
		new HelloClient().connect();
	}
}

class HelloClientHandler extends ChannelHandlerAdapter {

	private ByteBuf buf;
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		buf = ctx.alloc().buffer(15);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		buf.release();
		buf = null;
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		ByteBuf in = (ByteBuf) msg;
//		buf.writeBytes(in);
//		in.release();
//		in = null;
//		
//		while(buf.readableBytes() >= 15){
//			System.out.println(buf.readBytes(15).toString(CharsetUtil.UTF_8));
//		}
		
//		ByteBuf in = (ByteBuf) msg;
//		System.out.println(in.toString());
//		in.release();
		
		try {
			System.out.println(">>"+msg);
		} finally{
			ReferenceCountUtil.release(msg);
		}
	}
}

class HelloDecoder extends ByteToMessageDecoder{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if(in.readableBytes() < 15)
			return;
		out.add(in.readBytes(15).toString(CharsetUtil.UTF_8));
	}
}
