package com.wjl.decoder.string;

import io.netty.bootstrap.Bootstrap;
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
public class StringDecoderClient {
	
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
								new StringDecoder(CharsetUtil.UTF_8),//字符串解码器，将ByteBuf转成String，用于读入
								new StringEncoder(CharsetUtil.UTF_8),//字符串编码器，将String转成ByteBuf，用于写出 
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
		new StringDecoderClient().connect();
	}
	
}

//该类只保留最简单的逻辑 
class HelloStringClientHandler extends ChannelHandlerAdapter{
	private int counter = 1;
	private final String CR = System.getProperty("line.separator");
	private String data = "Hello World" + CR;
	
	//客户端和服务端TCP链路建立成功后，客户端往服务器发送100个消息 。 发送逻辑不变。
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i = 0 ; i < 100; i++){
			ctx.writeAndFlush(data);
		}
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.printf("收到第%d条回复%s\n", counter++, (String)msg);
		if(counter > 100)
			ctx.close();
	}

}

