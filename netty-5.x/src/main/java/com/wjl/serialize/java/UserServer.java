package com.wjl.serialize.java;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.HashMap;

import com.wjl.serialize.vo.UserReq;
import com.wjl.serialize.vo.UserResp;

/**
 * Netty使用Java原生序列化编解码例子的服务端。JDK原生序列化性能一般。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 *
 */
public class UserServer {
	
	private int port = 8000;

	private void service() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, 100)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectDecoder(1024*1024, 
							ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(new UserServerHandler());
				}
			 });
			
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private static HashMap<Integer, UserResp> data = new HashMap<Integer, UserResp>();
	static{
		data.put(1, new UserResp(1, "小天", 1, "超级英雄", "xiaoTian@163.com"));
		data.put(2, new UserResp(2, "心怡", 2, "软软妹子", "xinyi@126.com"));
		data.put(3, new UserResp(3, "小雪", 2, "天生丽质", "xiaoXue@163.com"));
		data.put(4, new UserResp(4, "小胖", 1, "霸道总裁", "xiapPang@qq.com"));
	}
	
	private static class UserServerHandler extends ChannelHandlerAdapter{
		//收到请求后，判断用户id，返回用户请求的User对象
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			UserReq req = (UserReq)msg;
			if(req == null) return;
			System.out.println("用户请求的id为：" + req.getId());
			ctx.writeAndFlush(data.get(req.getId()));
		}
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}
	
	public static void main(String[] args) {
		new UserServer().service();
	}

}
