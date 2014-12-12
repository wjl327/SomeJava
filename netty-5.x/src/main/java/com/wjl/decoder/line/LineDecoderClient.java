package com.wjl.decoder.line;

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
public class LineDecoderClient {
	
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
								new LineBasedFrameDecoder(1024), //以换行符为结束标志的解码器,设置一个最长长度，以免内存溢出
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
		new LineDecoderClient().connect();
	}
	
}

//该类只保留最简单的逻辑 
class HelloStringClientHandler extends ChannelHandlerAdapter{
	private int counter = 1;
	private final String CR = System.getProperty("line.separator");
	private byte[] req = ("Hello World" + CR).getBytes();
	
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

