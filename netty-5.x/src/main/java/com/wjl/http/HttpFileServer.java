package com.wjl.http;

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
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Netty开发Http服务端例子，处理文件的还没写。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 */
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
			System.out.printf("服务器启动后，请用浏览器访问%s:%d\n", host, port);
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
		public void messageReceived(ChannelHandlerContext ctx,
				FullHttpRequest request) throws Exception {
			if(!request.getMethod().toString().equals("GET"))
				System.out.println("REQ METHOD FAIL!!");
			
			String uri = request.getUri();
			String path = resolvePath(uri);
			
			if(path == null)
				return;
			
			System.out.println("请求URI：" + uri);
			
			File file = new File(path);
			if(!file.exists() || file.isHidden())
				System.out.println("File not found");
			
			if(file.isDirectory()){
				if(uri.endsWith("/"))
					respFileList(ctx, file);
				else{
					redirect(ctx, uri + '/');
				}
			}
			else{
				respFile(ctx, file);
			}
			
		}

		/**
		 * HTTP跳转，这样才支持相对路径
		 */
		private void redirect(ChannelHandlerContext ctx, String newUri) {
			FullHttpResponse response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
			response.headers().set(HttpHeaders.Names.LOCATION, newUri);
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
		
		/**
		 * 处理文件
		 */
		private void respFile(ChannelHandlerContext ctx, File file) {
//			RandomAccessFile raf = null;
//			long fileLength = 0;
//			try {
//				raf = new RandomAccessFile(file, "r");
//				fileLength = raf.length();
//			} catch (Exception e) {
//				e.printStackTrace();
//				return;
//			}
//			
//			FullHttpResponse response = new DefaultFullHttpResponse(
//					HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//			response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,  fileLength);
//			response.headers().set(HttpHeaders.Names.CONTENT_TYPE, null);
		}

		/**
		 * 组装目录逻辑
		 */
		private void respFileList(ChannelHandlerContext ctx, File parent) {
			StringBuilder html = new StringBuilder();
			html.append("<html xmlns='http://www.w3.org/1999/xhtml' lang='zh-CN'>");
			html.append("<head><title>" + parent.getName() + "</title></head>");
			html.append("<body>");
			html.append("<h3><a href='..'>..</a></h3>");
			html.append("<ul>");
			for(File file : parent.listFiles()){
				html.append("<li><a href='" + file.getName() + "'>" + file.getName() + "</a></li>");
			}
			html.append("</ul>");
			html.append("</body>");
			respHTML(ctx, html.toString());
		}

		/**
		 * 发送HTML
		 */
		private void respHTML(ChannelHandlerContext ctx, String html) {
			FullHttpResponse response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
			ByteBuf buf = Unpooled.copiedBuffer(html, CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}


		private String resolvePath(String uri) {
			try {
				uri = URLDecoder.decode(uri, CharsetUtil.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				System.out.println("utf-8解码失败~");
			}
			uri = uri.replace('/', File.separatorChar);
			if(uri.endsWith(".ico"))
				return null;
			return System.getProperty("user.dir") + uri;
		}
		
	}
	
	public static void main(String[] args) {
		new HttpFileServer().service();
	}

}
