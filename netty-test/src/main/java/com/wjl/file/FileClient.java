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
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

/**
 * 非二进制的文件传输，只是通过字符串传输文件内容。
 * 通过StringEncoder和LineBasedFrameDecoder传输文件内容.
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
					ch.pipeline().addLast(new ChunkedWriteHandler()); //文件传输
					ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8)); //支持ctx写字符串，不用读写ByteBuf  
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

class FileClientHandler extends SimpleChannelInboundHandler<HttpChunk>{
	
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

