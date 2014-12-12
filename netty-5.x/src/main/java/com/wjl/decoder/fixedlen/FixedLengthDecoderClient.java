package com.wjl.decoder.fixedlen;

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
public class FixedLengthDecoderClient {
	
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
						ch.pipeline().addLast(
								new FixedLengthFrameDecoder(2), //定长解码器
								new HelloStringClientHandler()); //业务Handler
					}
				});
			
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} finally{
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new FixedLengthDecoderClient().connect();
	}
	
}

//该类只保留最简单的逻辑 
class HelloStringClientHandler extends ChannelHandlerAdapter{
	private int counter = 1;
	private byte[] req = ("Hello World").getBytes();
	
	//客户端和服务端TCP链路建立成功后，客户端往服务器发送100个消息 。 发送逻辑不变。
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
		if(counter > 100)
			ctx.close();
	}

}

