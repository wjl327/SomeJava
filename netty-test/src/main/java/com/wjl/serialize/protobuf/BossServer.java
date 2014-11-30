package com.wjl.serialize.protobuf;

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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Netty使用 Google ProtoBuf 编解码的例子。 服务端。
 * .proto文件可以通过Ecplise插件+本地编译器，编辑后自动编译。
 * @author jl.wu
 *
 */
public class BossServer {
	
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
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(BossListProto.BossListReq.getDefaultInstance()));
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
					ch.pipeline().addLast(new BossServerHandler());
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
	
	private static class BossServerHandler extends ChannelHandlerAdapter{
		//收到请求后，判断用户id，返回用户请求的User对象
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			BossListProto.BossListReq req = (BossListProto.BossListReq)msg;
			System.out.println("用户请求参数id为：" + req.getId());
			ctx.writeAndFlush(resp(req.getId()));
		}
		private BossListProto.BossListResp resp(int id) {
			BossListProto.BossListResp.Builder bosses = BossListProto.BossListResp.newBuilder();
			if(id == 1){
				BossListProto.Boss.Builder boss1 = BossListProto.Boss.newBuilder();
				boss1.setId(1).setName("天龙").setTitle("霸气总裁").setEmail("TianLong@163.com");
				BossListProto.Boss.Builder boss2 = BossListProto.Boss.newBuilder();
				boss2.setId(2).setName("吴迪").setTitle("无敌总监").setEmail("Wudi@163.com");
				BossListProto.Boss.Builder boss3 = BossListProto.Boss.newBuilder();
				boss3.setId(2).setName("小深").setTitle("总裁夫人").setEmail("xiaoshen@163.com");
				bosses.addBosses(boss1).addBosses(boss2).addBosses(boss3);
			}
			return bosses.build();
		}
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}
	
	public static void main(String[] args) {
		new BossServer().service();
	}

}
