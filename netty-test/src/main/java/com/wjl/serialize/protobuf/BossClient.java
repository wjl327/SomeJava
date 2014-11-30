package com.wjl.serialize.protobuf;

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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Netty使用 Google ProtoBuf 编解码的例子。 客户端。
 * .proto文件可以通过Ecplise插件+本地编译器，编辑后自动编译。
 * @author jl.wu
 *
 */
public class BossClient {
	
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
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(BossListProto.BossListResp.getDefaultInstance()));
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
					ch.pipeline().addLast(new BossClientHandler());
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
		new BossClient().connect();
	}
	
}

class BossClientHandler extends ChannelHandlerAdapter{
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		BossListProto.BossListReq.Builder builder = BossListProto.BossListReq.newBuilder();
		builder.setId(0);
		ctx.writeAndFlush(builder.build());
		builder = BossListProto.BossListReq.newBuilder();
		builder.setId(1);
		ctx.writeAndFlush(builder.build());
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BossListProto.BossListResp resp = (BossListProto.BossListResp)msg;
		if(resp.getBossesList() == null || resp.getBossesCount() == 0)
			return;
		for(BossListProto.Boss boss : resp.getBossesList()){
			System.out.printf("Boss的名字:%s 职位:%s 邮箱:%s\n", boss.getName(), boss.getTitle(), boss.getEmail() );
		}
		ctx.close();
		System.exit(0);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
