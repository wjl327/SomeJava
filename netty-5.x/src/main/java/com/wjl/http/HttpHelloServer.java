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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Netty开发Http服务端。用最浏览器测试接口。
 * 使用到的页面是html/hello.html。
 * 该例子展示了处理GET和POST两种最常见的请求。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 */
public class HttpHelloServer {
	
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
					ch.pipeline().addLast("http-server-handler", new HelloHttpServerHandler());
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
	
	/**
	 * 简单的业务，一个首页，一个提交之后的页面。
	 */
	private static class HelloHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
		@Override
		protected void messageReceived(ChannelHandlerContext ctx,
				FullHttpRequest request) throws Exception {
			
			System.out.println("收到客户端请求：" + request.getMethod() + ":" + request.getUri());
			
			if(request.getMethod().toString().equals("GET")){
				
				StringBuilder rst = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader("html/hello.html"));
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
					Map<String, String> params = resolveParam(content);
					//组装返回
					rst.append("<html xmlns='http://www.w3.org/1999/xhtml' lang='zh-CN'><head><title>Your Page</title></head>");
					rst.append("<body align=center ><h1>");
					rst.append("Welcome to go home."); 
					if(params.get("sex")!=null && params.get("sex").equals("female"))
						rst.append("Mrs.");
					else
						rst.append("Mr.");
					rst.append(URLDecoder.decode(params.get("username"), "UTF-8"));
					rst.append("</h1></body></html>");
					resp(ctx, rst);
				}
				
			}
		}

		/**
		 * 解析POST参数 比较粗糙 
		 * @param content
		 * @return
		 */
		private Map<String, String> resolveParam(String content) {
			Map<String, String> rst = new HashMap<String, String>();
			if(content == null || content.trim().equals(""))
				return rst;
			String[] list = content.split("&");
			for(String param : list){
				String[] kw = param.split("=");
				rst.put(kw[0], kw[1]);
			}
			return rst;
		}

		/**
		 * 发送响应结果
		 * @param ctx
		 * @param rst
		 */
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
		new HttpHelloServer().service();
	}

}
