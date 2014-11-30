package com.wjl.serialize.java;

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
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.wjl.serialize.vo.UserReq;
import com.wjl.serialize.vo.UserResp;

/**
 * Netty使用Java原生序列化编解码的例子。 客户端。
 * 
 * @author jl.wu
 *
 */
public class UserClient {
	
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
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectDecoder(1024 * 1024,
							ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(new UserClientHandler());
				}
			 });
			//绑定端口，同步等待成功
			ChannelFuture f = b.connect(host, port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally{
			//释放连接池
			group.shutdownGracefully();
		}
	}
		
	public static void main(String[] args) throws Exception {
		new UserClient().connect();
	}
	
}

class UserClientHandler extends ChannelHandlerAdapter{
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i = 1; i < 5; i++){
			UserReq req = new UserReq();
			req.setId(i);
			ctx.writeAndFlush(req);
		}
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		UserResp resp = (UserResp)msg;
		if(resp == null) return;
		System.out.println("收到服务端返回：" + resp);
		if(resp.getId() == 4)
			ctx.close();
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
