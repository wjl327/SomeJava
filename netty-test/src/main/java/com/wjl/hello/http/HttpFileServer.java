package com.wjl.hello.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {
	
	private String host = "localhost";
	private int port = 8080;
	
	private void service() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());//Http请求解码器
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));//将多个消息转换成FullHttpRequest或FullHttpResponse对象
					ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());//Http响应编码器
					ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler()); //支持异步发送大的文件流，且不会占过多内存 
					ch.pipeline().addLast("http-server-handler", new HttpFileServerHandler());
				}
			});
			
			ChannelFuture future = b.bind(host, port).sync();
			future.channel().closeFuture().sync();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private static class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

		@Override
		protected void messageReceived(ChannelHandlerContext ctx,
				FullHttpRequest msg) throws Exception {
		}
		
	}
	
	public static void main(String[] args) {
		new HttpFileServer().service();
	}

}
