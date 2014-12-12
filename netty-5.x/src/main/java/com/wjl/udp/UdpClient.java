package com.wjl.udp;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * Netty UDP Demo
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 * 
 */
public class UdpClient {
	
	private int port = 8999;
	
	private void service() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioDatagramChannel.class)
			 .option(ChannelOption.SO_BROADCAST, true)
			 .handler(new JokeUDPClientHandler());
			 
			Channel channel = b.bind(0).sync().channel();
			
			//网段内广播UDP消息
			ByteBuf req = Unpooled.copiedBuffer("joke".getBytes());
			channel.writeAndFlush(new DatagramPacket(req, 
					new InetSocketAddress("255.255.255.255", port))).sync();
			
			if(!channel.closeFuture().await(15000)){
				System.out.println("Time out!!!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally{
			group.shutdownGracefully();
		}
	}
	
	private static class JokeUDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket>{
		@Override
		public void messageReceived(ChannelHandlerContext ctx,
				DatagramPacket msg) throws Exception {
			String resp = msg.content().toString(CharsetUtil.UTF_8);
			System.out.println(resp);
			ctx.close();
			System.exit(0);
		}
		
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}
	
	public static void main(String[] args) {
		new UdpClient().service();
	}

}
