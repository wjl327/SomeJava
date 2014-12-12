package com.wjl.hello2;

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
import io.netty.util.CharsetUtil;

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
public class HelloStringClient {
	
	private String host = "localhost";
	private int port = 8000;
	
	private void connect() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.TCP_NODELAY, true)
			 .handler(new ChannelInitializer<SocketChannel>(){
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new HelloStringClientHandler()); //只有业务Handler
					}
				});
			
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} finally{
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new HelloStringClient().connect();
	}
	
}

//该类只保留最简单的逻辑
class HelloStringClientHandler extends ChannelHandlerAdapter{
	private int counter = 1;
	//Hello World字符串
	private byte[] req = ("Hello World").getBytes();
	
	//客户端和服务端TCP链路建立成功后，客户端往服务器发送100个消息
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf buf = null;
		for(int i = 0 ; i < 100; i++){
			buf = Unpooled.buffer(req.length);
			buf.writeBytes(req);
			ctx.writeAndFlush(buf);
		}
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.printf("收到第%d条回复%s\n", counter++, ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
	}

}

