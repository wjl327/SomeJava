package com.wjl.hello.http;

import java.io.BufferedReader;
import java.io.FileReader;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class HelloHttpServer {
	
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
					ch.pipeline().addLast("http-server-handler", new HelloHttpServerHandler());
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
	
	private static class HelloHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
		@Override
		protected void messageReceived(ChannelHandlerContext ctx,
				FullHttpRequest request) throws Exception {
			
			System.out.println("收到客户端请求：" + request.getMethod() + ":" + request.getUri());
			
			if(request.getMethod().toString().equals("GET")){
				
				StringBuilder rst = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader("html/index.html"));
				String line = null;
				while((line = br.readLine()) != null)
					rst.append(line);
				br.close();
				
				resp(ctx, rst);
				
			}
			else if(request.getMethod().toString().equals("POST")){
				
				if(request.getUri().equals("/ok")){
					String content = request.content().toString(CharsetUtil.UTF_8);
					StringBuilder rst = new StringBuilder();
					//解析参数列表
					
					//
					resp(ctx, rst);
				}
				
			}
		}

		private void resp(ChannelHandlerContext ctx, StringBuilder rst) {
			FullHttpResponse response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			ByteBuf buf = Unpooled.copiedBuffer(rst, CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	public static void main(String[] args) {
		new HelloHttpServer().service();
	}

}
