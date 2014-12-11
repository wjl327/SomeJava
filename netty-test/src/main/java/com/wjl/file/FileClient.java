package com.wjl.file;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 官方没提供客户端例子。自己写的。其实就是简单接收字符串。
 * 
 * 文件客户端，可以用来测试FileServer1和FileServer2。
 * 通过StringDecoder和LineBasedFrameDecoder的组合一行行的接收字符串数据，并打印到控制台。
 * 
 */
public class FileClient {
	
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
			 .handler(new ChannelInitializer<SocketChannel>(){
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8)); //支持ctx写字符串，不用读写ByteBuf  
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024)); //以换行符为结束标志的解码器
					ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8)); //将接收到的对象转字符串
					ch.pipeline().addLast(new FileClientHandler());
				}
			 });
			
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} finally{
			//释放连接池
			group.shutdownGracefully();
		}
	}
		
	public static void main(String[] args) throws Exception {
		new FileClient().connect();
	}
	
}

class FileClientHandler extends SimpleChannelInboundHandler<String>{
	
	//必须换行符才LineBasedFrameDecoder才能解析成一行
	private static final String CR = System.getProperty("line.separator");
	
	//客户端和服务端TCP链路建立成功后，Netty会调用该方法
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush("index.html"+ CR);
	}
	
	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		System.out.println(msg);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.toString();
		ctx.close();
	}


}

